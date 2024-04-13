package com.example.budgetbuddy.Data.Remote

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.budgetbuddy.Data.Room.AuthUser
import com.example.budgetbuddy.Data.Room.Gasto
import com.example.budgetbuddy.Data.Room.User
import com.example.budgetbuddy.utils.toLong
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.HttpResponse
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import java.io.ByteArrayOutputStream
import java.io.IOException

import javax.inject.Inject
import javax.inject.Singleton

// https://medium.com/@nirazv/how-to-make-api-calls-using-ktor-with-android-kotlin-3c8caf8c6e3a

private val BASE_URL = "http://34.135.202.124:8000/"
@Serializable
data class PostUser(
    @SerialName("nombre") val nombre: String,
    @SerialName("email") val email: String,
    @SerialName("password") val password: String
)


/*******************************************************************************
 ****                               Exceptions                              ****
 *******************************************************************************/

class AuthenticationException : Exception()
class UserExistsException : Exception()
/*******************************************************************************
 ****                         Response Data Classes                         ****
 *******************************************************************************/

/**
 * Data class that represents server response when an [accessToken] is request.
 */

@Serializable
data class TokenInfo(
    @SerialName("access_token") val accessToken: String,
    @SerialName("expires_in") val expiresIn: Int,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("token_type") val tokenType: String,
)



@Serializable
data class PostGasto(
    @SerialName("nombre") val nombre: String,
    @SerialName("cantidad") val cantidad: Float,
    @SerialName("fecha") val fecha: Int,
    @SerialName("tipo") val tipo: String,
    @SerialName("latitud") val latitud: Float,
    @SerialName("longitud") val longitud: Float,
    @SerialName("user_id") val user_id: String,
    @SerialName("id") val id: String
)


/*******************************************************************************
 ****                          Bearer Token Storage                         ****
 *******************************************************************************/

/**
 * [MutableList] to save retrieves [BearerTokens]
 */
private val bearerTokenStorage = mutableListOf<BearerTokens>()
@Singleton
class HTTPService @Inject constructor() {
    /*******************************************************************************
    ##################################    INIT    ##################################
     *******************************************************************************/

    private val httpClient = HttpClient(CIO) {

        // If return code is not a 2xx then throw an exception
        expectSuccess = true

        // Install JSON handler (allows to receive and send JSON data)
        install(ContentNegotiation) { json() }

        // Handle non 2xx status responses
        HttpResponseValidator {
            handleResponseExceptionWithRequest { exception, _ ->
                when {
                    exception is ClientRequestException && exception.response.status == HttpStatusCode.Unauthorized -> Log.d("HTTP", exception.toString())
                    exception is ClientRequestException && exception.response.status == HttpStatusCode.Conflict -> Log.d("HTTP", exception.toString())
                    else -> {
                        exception.printStackTrace()
                        Log.d("HTTP", exception.toString())
                        throw exception
                    }
                }
            }
        }
    }
    suspend fun subscribeUser(FCMClientToken: String) {
        Log.d("SUBSCRIBE", FCMClientToken)
        httpClient.post("http://34.135.202.124:8000/notifications/subscribe/") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("fcm_client_token" to FCMClientToken))
        }
    }
    suspend fun sendNotificationToAll(content: String){
        httpClient.post("http://34.135.202.124:8000/notifications/"){
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToJsonElement(content))
        }
    }
    /*******************************************************************************
    ################################    USUARIOS    ################################
     *******************************************************************************/

    @Throws(IOException::class, UserExistsException::class)
    suspend fun createUser(user: AuthUser): Boolean {
        Log.d("HTTP", user.toString())
        return withContext(Dispatchers.IO) {
            try {
                val response = httpClient.post("http://34.135.202.124:8000/users/") {
                    contentType(ContentType.Application.Json)
                    setBody(Json.encodeToJsonElement(user))
                }
                Log.d("HTTP", response.status.toString())
                true
            } catch (e: IOException) {
                // Captura la excepción en caso de que no se pueda acceder al servidor
                Log.e("HTTP", "Error de red: ${e.message}")
                false // Retorna null indicando que no se encontró el usuario
            }
        }
    }


    @Throws(IOException::class)
    suspend fun getUserByEmail(email: String): AuthUser? {
        return withContext(Dispatchers.IO) {
            try {
                val response = httpClient.get("http://34.135.202.124:8000/users/$email")
                response.body()
            } catch (e: ClientRequestException) {
                // Manejar la excepción específica de ClientRequestException
                Log.e("HTTP", "Error de red 1: ${e.message}")
                AuthUser("", "", "") // Retorna null indicando que no se encontró el usuario
            } catch (e: IOException) {
                // Manejar otras excepciones de E/S
                Log.e("HTTP", "Error de red 2: ${e.message}")
                null // Retorna null indicando que no se encontró el usuario
            }
        }
    }

    @Throws(Exception::class)
    suspend fun editUser(email: String, user: AuthUser): AuthUser? = runBlocking {
        val response = httpClient.put("http://34.135.202.124:8000/users/${email}") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }
        response.body()
    }

    //----------   Imágen de perfil   ----------//

    suspend fun getUserProfile(email: String): Bitmap {
        val response = httpClient.get("http://34.135.202.124:8000/profile/${email}")
        val image: ByteArray = response.body()
        return BitmapFactory.decodeByteArray(image, 0, image.size)
    }

    suspend fun setUserProfile(email: String, image: Bitmap) {
        val stream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()
        httpClient.submitFormWithBinaryData(
            url = "http://34.135.202.124:8000/profile/${email}",
            formData = formData {
                append("file", byteArray, Headers.build {
                    append(HttpHeaders.ContentType, "image/png")
                    append(HttpHeaders.ContentDisposition, "filename=profile_image.png")
                })
            }
        ) { method = HttpMethod.Put }
    }

    /*******************************************************************************
    #################################    GASTOS    #################################
    *******************************************************************************/

    /**
     * Este método descarga los datos del usuario en el momento o el [currentUser].
     * Se utiliza al hacer login por primera vez en el teléfono, cuando ya existían datos en la nube
     * como al conectarse a budgetbuddy46@gmail.com o al descargarse la copia de seguridad de la nube.*/
    @Throws(Exception::class)
    suspend fun download_user_data(email: String): List<PostGasto>? = runBlocking {
        Log.d("HTTP", email)
        val response = httpClient.get("http://34.135.202.124:8000/gastos/$email/")
        Log.d("HTTP", response.status.toString())
        response.body()
    }

    @Throws(Exception::class)
    suspend fun delete_user_data(email: String){
        httpClient.delete("http://34.135.202.124:8000/gastos/$email/")
    }

    @Throws(Exception::class)
    suspend fun upload_gasto(email: String, gasto: PostGasto): PostGasto?= runBlocking{
        val response = httpClient.post("http://34.135.202.124:8000/gastos/$email/") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToJsonElement(gasto))
        }
        response.body()
    }
}