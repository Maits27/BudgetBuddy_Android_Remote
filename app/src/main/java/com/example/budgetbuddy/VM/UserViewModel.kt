package com.example.budgetbuddy.VM

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.budgetbuddy.Data.Repositories.IGastoRepository
import com.example.budgetbuddy.Data.Room.User
import com.example.budgetbuddy.Data.Repositories.UserRepository
import com.example.budgetbuddy.Data.Room.AuthUser
import com.example.budgetbuddy.UserVerification.correctEmail
import com.example.budgetbuddy.UserVerification.correctName
import com.example.budgetbuddy.UserVerification.correctPasswd
import com.example.budgetbuddy.utils.hash
import dagger.hilt.android.lifecycle.HiltViewModel
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
//    init {
//        viewModelScope.launch {
//            Log.d("AÑADIR USUARIO", "TODO OK0000!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
//            añadirUsuario("BudgetBuddy", "budgetbuddy46@gmail.com", "123")
//            Log.d("AÑADIR USUARIO", "TODO OK!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
//        }
//    }

    /*************************************************
     **                    Eventos                  **
     *************************************************/


    ////////////////////// Añadir y eliminar elementos //////////////////////

    suspend fun añadirUsuario(nombre: String, email: String, passwd: String): AuthUser {
        val user = AuthUser(nombre, email, passwd.hash())
        try {
            userRepository.insertUsuario(user)
        }catch (e: Exception){
            Log.d("BASE DE DATOS!", e.toString())
        }
        return user
    }

    suspend fun borrarUsuario(user: User){
        userRepository.deleteUsuario(user)
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

    suspend fun correctRegister(nombre: String, email: String, p1:String, p2:String): Boolean{
        val ok0 = correctName(nombre)
        val ok1 = correctEmail(email)
        val ok2 = correctPasswd(p1, p2)
        if (ok0 && ok1 && ok2){
            if(userRepository.userName(email)==""){
                añadirUsuario(nombre, email, p1)
                return true
            }
        }
        return false
    }


}