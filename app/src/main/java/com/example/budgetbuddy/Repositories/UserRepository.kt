package com.example.budgetbuddy.Repositories

import android.graphics.Bitmap
import android.util.Log
import com.example.budgetbuddy.Local.DAO.UserDao
import com.example.budgetbuddy.Local.Data.AuthUser
import com.example.budgetbuddy.Remote.HTTPService
import com.example.budgetbuddy.Local.Room.User
import com.example.budgetbuddy.utils.user_to_authUser
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

/******************************************************************
 * Interfaz que define la API del listado de gastos - Repositorio
 * Los métodos definidos son las acciones posibles para interactuar
 * con la BBDD
 *******************************************************************/
interface IUserRepository: ILoginSettings {
    fun logIn(email: String, login:Boolean): AuthUser?
    suspend fun isLogged(email: String):Boolean?
    suspend fun exists(email: String):Boolean
    suspend fun insertLocal(user: User)
    suspend fun insertUsuario(user: AuthUser): Boolean
    suspend fun deleteUsuario(user: User): Int
    fun todosLosUsuarios(): Flow<List<User>>
    suspend fun userNamePassword(email: String, passwd:String): HashMap<String, Any>
    fun userName(email: String): String
    suspend fun editarUsuario(user: User): Int
    suspend fun getUserProfile(email: String): Bitmap
    suspend fun setUserProfile(email: String, image: Bitmap): Bitmap
}
/**
 * Implementación de [IGastoRepository] que usa Hilt para inyectar los
 * parámetros necesarios. Desde aquí se accede a [GastoDao], que se encarga
 * de la conexión a la BBDD de Room.
 * */
@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val httpService: HTTPService,
    private val loginSettings: ILoginSettings
) : IUserRepository {

    /********************************************************************
     ********************         Variables         ********************
     ********************************************************************/

    lateinit var profileImage: Bitmap

    /********************************************************************
     ******************         LastLoggedUser         ******************
     ********************************************************************/
    override suspend fun getLastLoggedUser(): String? = loginSettings.getLastLoggedUser()
    override suspend fun setLastLoggedUser(user: String) = loginSettings.setLastLoggedUser(user)

    /********************************************************************
     *****************          LOCAL + REMOTO          *****************
     ********************************************************************/

    override suspend fun insertUsuario(user: AuthUser): Boolean{
        try {
            val remote = httpService.createUser(user)
            if (remote) userDao.insertUsuario(User(nombre = user.nombre, email = user.email, password = user.password))
            return remote
        }catch (e: Exception){
        }
        return false
    }

    /********************************************************************
     **********************         REMOTO         **********************
     ********************************************************************/
    ////////////////////// Sesión usuario //////////////////////

    override fun logIn(email: String, login: Boolean): AuthUser? = runBlocking{
        httpService.loginUser(email, login)
    }
    ////////////////////// Info usuario //////////////////////

    override suspend fun isLogged(email: String): Boolean? {
        return httpService.isLogged(email)
    }

    override suspend fun exists(email: String): Boolean {
        val user = httpService.getUserByEmail(email)
        if ((user?.email ?: "") != ""){
            return true
        }
        return false
    }



    override suspend fun userNamePassword(email: String, passwd:String): HashMap<String, Any> {
        var result = HashMap<String, Any>()
        result["user"] = AuthUser("", "", "")
        result["runtime"] = true

        val remoto = httpService.getUserByEmail(email)
        // Si es "" es que ha conseguido respuesta nula del servidor (login incorrecto, pero server activo)
        if ( (remoto?.email ?: " ")!=" " ){result["runtime"] = false}

        if( passwd == remoto?.password ){
            val nombre = remoto.nombre
            result["user"] = AuthUser(nombre, email, passwd)
        }
        return result
    }

    ////////////////////// Editar usuario //////////////////////
    override suspend fun editarUsuario(user: User): Int {
        httpService.editUser(
            user.email,
            user_to_authUser(user)
        )
        return userDao.editarUsuario(user)
    }

    ////////////////////// Imagen perfil usuario //////////////////////
    override suspend fun getUserProfile(email: String): Bitmap {
        try {
            profileImage = httpService.getUserProfile(email)
        } catch (e: ResponseException) {
            Log.e("HTTP", "Couldn't get profile image.")
            e.printStackTrace()
        }
        return profileImage
    }

    override suspend fun setUserProfile(email: String, image: Bitmap): Bitmap {
        try {
            httpService.setUserProfile(email, image)
            profileImage = image
        } catch (e: ResponseException) {
            Log.e("HTTP", "Couldn't upload profile image.")
            e.printStackTrace()
        }
        return profileImage
    }


    /********************************************************************
     **********************         LOCAL          **********************
     ********************************************************************/
    ////////////////////// Insertar usuario //////////////////////
    override suspend fun insertLocal(user: User) {
        userDao.insertUsuario(user)
    }

    ////////////////////// Borrar usuario //////////////////////
    override suspend fun deleteUsuario(user: User): Int {
        return userDao.deleteUsuario(user)
    }

    ////////////////////// Info usuarios //////////////////////
    override fun todosLosUsuarios(): Flow<List<User>> {
        return userDao.todosLosUsuarios()
    }

    override fun userName(email: String): String {
        return userDao.userName(email)?:""
    }




}

