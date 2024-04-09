package com.example.budgetbuddy.utils

import android.location.Location
import android.util.Log
import androidx.room.TypeConverter
import com.example.budgetbuddy.Data.Enumeration.TipoGasto
import com.example.budgetbuddy.Data.Enumeration.obtenerTipoDeNombre
import com.example.budgetbuddy.Data.Enumeration.obtenerTipoEnIdioma
import com.example.budgetbuddy.Data.Remote.PostGasto
import com.example.budgetbuddy.Data.Room.AuthUser
import com.example.budgetbuddy.Data.Room.Gasto
import com.example.budgetbuddy.Data.Room.User
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import java.math.BigInteger
import java.security.MessageDigest
import java.time.LocalDate


/**
 * Funciones de tipo [LocalDate] y [Long] para conversión entre ellos
 * fuera del entorno ROOM.
 */
fun LocalDate.toLong(): Long{
    return this.toEpochDay()
}

fun Long.toLocalDate(): LocalDate {
    return this.let { LocalDate.ofEpochDay(this) }
}

private val md = MessageDigest.getInstance("SHA-512")


/**
 * Comparar hash con [String] y conversor de [String] a hash
 */

fun String.hash(): String{
    val messageDigest = md.digest(this.toByteArray())
    val no = BigInteger(1, messageDigest)
    var hashText = no.toString(16)
    while (hashText.length < 32) {
        hashText = "0$hashText"
    }
    return hashText
}

fun String.compareHash(hash:String): Boolean{
    return this.hash() == hash
}


/**
 * Conversión entre las clases [PostGasto] y [Gasto]
 */

fun convertirPostGastos_Gastos(listado: List<PostGasto>?): List<Gasto>{
    val resultado = mutableListOf<Gasto>()
    for(pgasto in listado?: emptyList()){
        var tipo = obtenerTipoDeNombre(pgasto.tipo)
        if(tipo!=null){
            resultado.add(postGasto_gasto(pgasto, tipo))
        }else{
            Log.d("ERROR DE TIPO", "TIPO: ${pgasto.tipo} NO EXISTE!!!!!!!!!!1")
        }
    }
    return resultado
}
fun postGasto_gasto(pgasto: PostGasto, tipo: TipoGasto): Gasto{
    return Gasto(
        nombre = pgasto.nombre,
        cantidad = pgasto.cantidad.toDouble(),
        tipo = tipo,
        fecha = pgasto.fecha.toLong().toLocalDate(),
        latitud = pgasto.latitud.toDouble(),
        longitud = pgasto.longitud.toDouble(),
        userId = pgasto.user_id,
        id = pgasto.id
    )
}

fun convertirGastos_PostGastos(listado: List<Gasto>?): List<PostGasto>{
    val resultado = mutableListOf<PostGasto>()
    for(gasto in listado?: emptyList()){
        resultado.add(gasto_postGastos(gasto))
    }
    return resultado
}

fun gasto_postGastos(gasto: Gasto): PostGasto{
    return  PostGasto(
        nombre = gasto.nombre,
        cantidad = gasto.cantidad.toFloat(),
        tipo = obtenerTipoEnIdioma(gasto.tipo, "es"),
        fecha = gasto.fecha.toLong().toInt(),
        latitud = gasto.latitud.toFloat(),
        longitud = gasto.longitud.toFloat(),
        user_id = gasto.userId,
        id = gasto.id
        )
}

private fun fromLocation(location: Location?): String {
    val gson = Gson()
    return location?.let { gson.toJson(it) }?:""
}

private fun toLocation(locationString: String?): Location? {
    val gson = Gson()
    if (locationString=="") return null
    return locationString?.let { gson.fromJson(it, Location::class.java) }
}

fun locationToLatLng(location: Location): LatLng {
    return LatLng(location.latitude, location.longitude)
}

fun user_to_authUser(user: User?): AuthUser{
    if (user==null) return AuthUser("", "", "")
    else return AuthUser(
        nombre = user.nombre,
        email = user.email,
        password = user.password
    )
}

fun authuser_to_user(user: AuthUser?): User{
    if (user==null) return User("", "", "")
    else return User(
        nombre = user.nombre,
        email = user.email,
        password = user.password
    )
}