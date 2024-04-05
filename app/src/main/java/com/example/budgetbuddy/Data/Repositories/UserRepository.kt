package com.example.budgetbuddy.Data.Repositories

import android.graphics.Bitmap
import android.util.Log
import com.example.budgetbuddy.Data.DAO.UserDao
import com.example.budgetbuddy.Data.Remote.HTTPService
import com.example.budgetbuddy.Data.Room.AuthUser
import com.example.budgetbuddy.Data.Room.User
import com.example.budgetbuddy.utils.compareHash
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.Json.Default.encodeToString
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.POST
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

/******************************************************************
 * Interfaz que define la API del listado de gastos - Repositorio
 * Los métodos definidos son las acciones posibles para interactuar
 * con la BBDD
 *******************************************************************/
interface IUserRepository {
    suspend fun insertUsuario(user: AuthUser)
    suspend fun deleteUsuario(user: User): Int
    fun todosLosUsuarios(): Flow<List<User>>
    suspend fun userNamePassword(email: String, passwd:String): HashMap<String, Any>
    fun userName(email: String): String
    fun editarUsuario(user: User): Int

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
    private val httpService: HTTPService
) : IUserRepository {
    lateinit var profileImage: Bitmap
    override suspend fun insertUsuario(user: AuthUser){
        Log.d("REPO", "INSERT!!!!!!!!!!!!!!!!!!!!")
        try {
            httpService.createUser(user)
        }catch (e: Exception){
            Log.d("REPO ERROR", "$e!!!!!!!!!!!!!!!!!!!!")
        }
        return userDao.insertUsuario(User(nombre = user.nombre, email = user.email, password = user.password))
    }

    override suspend fun deleteUsuario(user: User): Int {
        return userDao.deleteUsuario(user)
    }

    override fun todosLosUsuarios(): Flow<List<User>> {
        return userDao.todosLosUsuarios()
    }


    override suspend fun userNamePassword(email: String, passwd:String): HashMap<String, Any> {
        var result = HashMap<String, Any>()
        result["nombre"] = ""
        result["bajar_datos"] = false

        val remoto = httpService.getUserByEmail(email)
        if( remoto?.password==passwd ){
            val nombre = remoto.nombre
            result["nombre"] = nombre
            val local = userDao.usernamePassword(email, passwd)?: ""
            if (local == "") {
                result["bajar_datos"] = true
                userDao.insertUsuario(User(
                    nombre = nombre,
                    email= email,
                    password = passwd
                ))
            }
        }
        return result
    }

    override fun userName(email: String): String {
        return userDao.userName(email)?:""
    }

    override fun editarUsuario(user: User): Int {
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

