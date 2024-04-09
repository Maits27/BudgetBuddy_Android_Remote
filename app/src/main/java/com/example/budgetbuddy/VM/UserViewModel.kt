package com.example.budgetbuddy.VM

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetbuddy.Data.Repositories.IGastoRepository
import com.example.budgetbuddy.Data.Repositories.IUserRepository
import com.example.budgetbuddy.Data.Room.User
import com.example.budgetbuddy.Data.Repositories.UserRepository
import com.example.budgetbuddy.Data.Room.AuthUser
import com.example.budgetbuddy.MainActivity
import com.example.budgetbuddy.UserVerification.correctEmail
import com.example.budgetbuddy.UserVerification.correctName
import com.example.budgetbuddy.UserVerification.correctPasswd
import com.example.budgetbuddy.utils.hash
import com.example.budgetbuddy.utils.user_to_authUser
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

/********************************************************
 ****                 App View Model                 ****
 ********************************************************/
/**
 * View Model de Hilt para los datos del usuario
 * Encargado de las interacciones entre el frontend de la app y el repositorio [gastoRepository] que realiza los cambios en ROOM.
 *
 * @gastoRepository: implementación de [IGastoRepository] y repositorio a cargo de realizar los cambios en la BBDD.
 */
@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: IUserRepository
) : ViewModel() {


    private val todosLosUsuarios = userRepository.todosLosUsuarios()
    val lastLoggedUser: String? = runBlocking { return@runBlocking userRepository.getLastLoggedUser() }
    var profilePicture: Bitmap? by mutableStateOf(null)
        private set

    var currentUser by mutableStateOf(AuthUser("", "", ""))


    /*************************************************
     **                    Eventos                  **
     *************************************************/

    fun updateLastLoggedUsername(user: String) = runBlocking {
        Log.d("COMPARE USERS", "SET USER: $user")
        userRepository.setLastLoggedUser(user)
    }


    ////////////////////// Añadir y eliminar elementos //////////////////////

    private suspend fun añadirUsuario(nombre: String, email: String, passwd: String): Boolean {
        return withContext(Dispatchers.IO) {
            val user = AuthUser(nombre, email, passwd.hash())
            try {
                val remote = userRepository.insertUsuario(user)
                if(remote) profilePicture = userRepository.getUserProfile(email)
                remote
            }catch (e: Exception){
                Log.d("BASE DE DATOS!", e.toString())
                false
            }
        }
    }

    suspend fun borrarUsuario(user: User){
        userRepository.deleteUsuario(user)
    }
    fun logout(context: Context){
        profilePicture = null
        currentUser = AuthUser("", "", "")
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("LOGGED_USERNAME", "")
        }
        context.startActivity(intent)
        (context as Activity).finish()
    }

    ////////////////////// Editar elementos //////////////////////
    fun cambiarDatos(user: User) {
        viewModelScope.launch {  userRepository.editarUsuario(user) }
        currentUser = user_to_authUser(user)
    }

    suspend fun correctLogIn(email:String, passwd: String): HashMap<String, Any>{
        if(email!="" && passwd!=""){
            return userRepository.userNamePassword(email, passwd.hash())
        }
        var r = HashMap<String, Any>()
        r["user"] = AuthUser("", "", "")
        r["runtime"] = false
        r["bajar_datos"] = false
        return  r
    }
    suspend fun correctRegister(nombre: String, email: String, p1:String, p2:String): HashMap<String, Boolean>{
        val result = HashMap<String, Boolean>()
        result["server"] = true
        result["name"] = correctName(nombre)
        result["email"] = correctEmail(email)
        result["password"] = correctPasswd(p1, p2)
        if (result["name"]!! && result["email"]!! && result["password"]!!){
            if(userRepository.userName(email)==""){
                Log.d("Vacio", "user vacio")
                result["server"] = añadirUsuario(nombre, email, p1)
                return result
            }
            Log.d("NO Vacio", "user NO VACIO: ${userRepository.userName(email)}")
        }
        return result
    }

    fun setProfileImage(email: String, image: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            profilePicture = null
            profilePicture = userRepository.setUserProfile(email, image)
        }
    }

    fun getProfileImage(email: String){
        viewModelScope.launch(Dispatchers.IO) {
            delay(100)
            profilePicture = userRepository.getUserProfile(email)
        }
    }


}