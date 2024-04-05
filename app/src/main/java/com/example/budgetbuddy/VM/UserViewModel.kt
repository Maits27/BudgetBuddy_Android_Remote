package com.example.budgetbuddy.VM

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetbuddy.Data.Repositories.IGastoRepository
import com.example.budgetbuddy.Data.Room.User
import com.example.budgetbuddy.Data.Repositories.UserRepository
import com.example.budgetbuddy.Data.Room.AuthUser
import com.example.budgetbuddy.UserVerification.correctEmail
import com.example.budgetbuddy.UserVerification.correctName
import com.example.budgetbuddy.UserVerification.correctPasswd
import com.example.budgetbuddy.utils.hash
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
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
    private val userRepository: UserRepository
) : ViewModel() {


    private val todosLosUsuarios = userRepository.todosLosUsuarios()
    var profilePicture: Bitmap? by mutableStateOf(null)
        private set

    var profilePicturePath: String? = null
    var currentUserName by mutableStateOf("")


    /*************************************************
     **                    Eventos                  **
     *************************************************/


    ////////////////////// Añadir y eliminar elementos //////////////////////

    fun añadirUsuario(nombre: String, email: String, passwd: String) {
        viewModelScope.launch {
            val user = AuthUser(nombre, email, passwd.hash())
            try {
                userRepository.insertUsuario(user)
                profilePicture = userRepository.getUserProfile(email)
            }catch (e: Exception){
                Log.d("BASE DE DATOS!", e.toString())
            }
        }

    }

    suspend fun borrarUsuario(user: User){
        userRepository.deleteUsuario(user)
    }
    fun logout(){
        profilePicture = null
        profilePicturePath = null
    }

    ////////////////////// Editar elementos //////////////////////
    fun cambiarDatos(user: User) {
        userRepository.editarUsuario(user)
    }

    suspend fun correctLogIn(email:String, passwd: String): HashMap<String, Any>{
        if(email!="" && passwd!=""){
            return userRepository.userNamePassword(email, passwd.hash())
        }
        var r = HashMap<String, Any>()
        r["nombre"] = ""
        r["bajar_datos"] = false
        return  r
    }

    fun correctRegister(nombre: String, email: String, p1:String, p2:String): Boolean{
        val ok0 = correctName(nombre)
        val ok1 = correctEmail(email)
        val ok2 = correctPasswd(p1, p2)
        if (ok0 && ok1 && ok2){
            Log.d("Registro: " ,"Ok: $ok0 $ok1 $ok2")
            if(userRepository.userName(email)==""){
                Log.d("Vacio", "user vacio")
                añadirUsuario(nombre, email, p1)
                return true
            }
            Log.d("NO Vacio", "user NO VACIO: ${userRepository.userName(email)}")
        }
        return false
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