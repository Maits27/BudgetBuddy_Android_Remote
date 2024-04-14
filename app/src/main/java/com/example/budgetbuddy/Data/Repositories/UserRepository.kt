package com.example.budgetbuddy.Data.Repositories

import android.graphics.Bitmap
import android.util.Log
import com.example.budgetbuddy.Data.DAO.UserDao
import com.example.budgetbuddy.Data.Remote.HTTPService
import com.example.budgetbuddy.Data.Room.AuthUser
import com.example.budgetbuddy.Data.Room.User
import com.example.budgetbuddy.utils.compareHash
import com.example.budgetbuddy.utils.hash
import com.example.budgetbuddy.utils.user_to_authUser
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/******************************************************************
 * Interfaz que define la API del listado de gastos - Repositorio
 * Los métodos definidos son las acciones posibles para interactuar
 * con la BBDD
 *******************************************************************/
interface IUserRepository: ILoginSettings {
    suspend fun logIn(email: String, login:Boolean):AuthUser?
    suspend fun isLogged(email: String):Boolean?
    suspend fun exists(email: String):Boolean
    suspend fun insertLocal(user: User)
    suspend fun insertUsuario(user: AuthUser): Boolean
    suspend fun deleteUsuario(user: User): Int
    fun todosLosUsuarios(): Flow<List<User>>
    suspend fun userNamePassword(email: String, passwd:String): HashMap<String, Any>
    suspend fun userXEmail(email: String):AuthUser?
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
    lateinit var profileImage: Bitmap

    override suspend fun getLastLoggedUser(): String? = loginSettings.getLastLoggedUser()
    override suspend fun setLastLoggedUser(user: String) = loginSettings.setLastLoggedUser(user)
    override suspend fun logIn(email: String, login: Boolean): AuthUser? {
        return httpService.loginUser(email, login)
    }

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

    override suspend fun insertLocal(user: User){
        userDao.insertUsuario(user)
    }

    override suspend fun insertUsuario(user: AuthUser): Boolean{
        Log.d("REPO", "INSERT!!!!!!!!!!!!!!!!!!!!")
        try {
            val remote = httpService.createUser(user)
            if (remote) userDao.insertUsuario(User(nombre = user.nombre, email = user.email, password = user.password))
            return remote
        }catch (e: Exception){
            Log.d("REPO ERROR", "$e!!!!!!!!!!!!!!!!!!!!")
        }
        return false
    }

    override suspend fun deleteUsuario(user: User): Int {
        return userDao.deleteUsuario(user)
    }

    override fun todosLosUsuarios(): Flow<List<User>> {
        return userDao.todosLosUsuarios()
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

    override suspend fun userXEmail(email: String): AuthUser? {
        return httpService.getUserByEmail(email)
    }

    override fun userName(email: String): String {
        return userDao.userName(email)?:""
    }

    override suspend fun editarUsuario(user: User): Int {
        httpService.editUser(
            user.email,
            user_to_authUser(user)
        )
        return userDao.editarUsuario(user)
    }

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


}

