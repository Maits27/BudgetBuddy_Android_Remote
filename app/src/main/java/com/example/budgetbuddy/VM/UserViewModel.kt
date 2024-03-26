package com.example.budgetbuddy.VM

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetbuddy.Data.IGastoRepository
import com.example.budgetbuddy.Data.User
import com.example.budgetbuddy.Data.UserRepository
import com.example.budgetbuddy.UserVerification.correctEmail
import com.example.budgetbuddy.UserVerification.correctName
import com.example.budgetbuddy.UserVerification.correctPasswd
import com.example.budgetbuddy.utils.compareHash
import com.example.budgetbuddy.utils.hash
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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
    init {
        viewModelScope.launch {
            Log.d("AÑADIR USUARIO", "TODO OK0000!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
            añadirUsuario("User1", "a@a.com", "123")
            Log.d("AÑADIR USUARIO", "TODO OK!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
        }
    }

    /*************************************************
     **                    Eventos                  **
     *************************************************/


    ////////////////////// Añadir y eliminar elementos //////////////////////

    suspend fun añadirUsuario(nombre: String, email: String, passwd: String): User {
        val user = User(nombre, email, passwd.hash())
        try {
            userRepository.insertUsuario(user)
        }catch (e: Exception){
            Log.d("BASE DE DATOS!", e.toString())
        }
        return user
    }

    suspend fun borrarUsuario(user:User){
        userRepository.deleteUsuario(user)
    }

    ////////////////////// Editar elementos //////////////////////
    fun cambiarDatos(user:User) {
        userRepository.editarUsuario(user)
    }

   fun correctLogIn(email:String, passwd: String): String{
        return userRepository.userNamePassword(email, passwd.hash())
    }

    suspend fun correctRegister(nombre: String, email: String, p1:String, p2:String): Boolean{
        val ok0 = correctName(nombre)
        val ok1 = correctEmail(email)
        val ok2 = correctPasswd(p1, p2)
        Log.d("VIEWMODEL", "DATOS OK: $nombre $email $p1 $p2")
        Log.d("VIEWMODEL", "DATOS OK: $ok0 $ok1 $ok2")
        if (ok0 && ok1 && ok2){
            Log.d("REGISTRO", "TODO OK 1")
            if(userRepository.userName(email)==""){
                Log.d("REGISTRO", "TODO OK 2")
                añadirUsuario(nombre, email, p1)
                return true
            }
        }
        return false
    }


}