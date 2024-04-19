package com.example.budgetbuddy.Remote

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.budgetbuddy.Local.Data.AuthUser
import com.example.budgetbuddy.Local.Data.PostGasto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import java.io.ByteArrayOutputStream
import java.io.IOException

import javax.inject.Inject
import javax.inject.Singleton

// Link documentación: https://medium.com/@nirazv/how-to-make-api-calls-using-ktor-with-android-kotlin-3c8caf8c6e3a


/*******************************************************************************
 ****                               Excepciones                             ****
 *******************************************************************************/

class UserExistsException : Exception()

/*******************************************************************************
 ****                              Cliente HTTP                             ****
 *******************************************************************************/
/**             (Requisito obligatorio)           **/
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
    /*******************************************************************************
    ##########################    SUBSCRIPCIÓN FIREBASE    ##########################
     *******************************************************************************/
    suspend fun subscribeUser(FCMClientToken: String) {
        httpClient.post("http://34.135.202.124:8000/notifications/subscribe/") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("fcm_client_token" to FCMClientToken))
        }
    }

//    suspend fun sendNotificationToAll(content: String){
//        httpClient.post("http://34.135.202.124:8000/notifications/"){
//            contentType(ContentType.Application.Json)
//            setBody(Json.encodeToJsonElement(content))
//        }
//    }
    /*******************************************************************************
    ################################    USUARIOS    ################################
     *******************************************************************************/

    ////////////////////// Comprobación y edición del estado de login //////////////////////
    suspend fun isLogged(email: String): Boolean? {
        if (email=="") return false
        val response = httpClient.get("http://34.135.202.124:8000/login/${email}")
        return response.body()
    }
    @Throws(IOException::class)
    suspend fun loginUser(email: String, login:Boolean): AuthUser? {
        return withContext(Dispatchers.IO){
            try {
                val response = httpClient.post("http://34.135.202.124:8000/login/$email?login=$login") {
                    contentType(ContentType.Application.Json)
                }
                response.body()
            } catch (e: IOException) {
                // Captura la excepción en caso de que no se pueda acceder al servidor
                Log.e("HTTP", "Error de red: ${e.message}")
                null // Retorna null indicando que no se encontró el usuario
            }
        }
    }

    ////////////////////// Creación de usuario //////////////////////
    @Throws(IOException::class, UserExistsException::class)
    suspend fun createUser(user: AuthUser): Boolean {
        Log.d("HTTP", user.toString())
        return withContext(Dispatchers.IO) {
            try {
                val response = httpClient.post("http://34.135.202.124:8000/users/") {
                    contentType(ContentType.Application.Json)
                    setBody(Json.encodeToJsonElement(user))
                }
                true
            } catch (e: IOException) {
                // Captura la excepción en caso de que no se pueda acceder al servidor
                Log.e("HTTP", "Error de red: ${e.message}")
                false // Retorna null indicando que no se encontró el usuario
            }
        }
    }

    ////////////////////// Información del usuario //////////////////////
    @Throws(IOException::class)
    suspend fun getUserByEmail(email: String): AuthUser? {
        return withContext(Dispatchers.IO) {
            try {
                val response = httpClient.get("http://34.135.202.124:8000/users/$email")
                response.body()
            } catch (e: ClientRequestException) {
                AuthUser("", "", "") // Retorna null indicando que no se encontró el usuario
            } catch (e: IOException) {
                null // Retorna null indicando que no se encontró el usuario
            }
        }
    }

    ////////////////////// Edición del usuario //////////////////////
    @Throws(Exception::class)
    suspend fun editUser(email: String, user: AuthUser): AuthUser? = runBlocking {
        val response = httpClient.put("http://34.135.202.124:8000/users/${email}") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }
        response.body()
    }

    /*******************************************************************************
    ########################    PERFIL DE USUARIO (FOTO)    ########################
     *******************************************************************************/
    /**             (Requisito obligatorio)           **/

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

    ////////////////////// Descarga de todos los datos //////////////////////
    @Throws(Exception::class)
    suspend fun download_user_data(email: String): List<PostGasto>? = runBlocking {
        val response = httpClient.get("http://34.135.202.124:8000/gastos/$email/")
        response.body()
    }

    ////////////////////// Borrado de todos los datos //////////////////////
    @Throws(Exception::class)
    suspend fun delete_user_data(email: String){
        httpClient.delete("http://34.135.202.124:8000/gastos/$email/")
    }


    ////////////////////// Actualización de todos los datos //////////////////////
    @Throws(Exception::class)
    suspend fun upload_gastos(email: String, gastos: List<PostGasto>) = runBlocking{
        httpClient.post("http://34.135.202.124:8000/gastos_upload/$email/") {
            contentType(ContentType.Application.Json)
            setBody(gastos)
        }

    }
}