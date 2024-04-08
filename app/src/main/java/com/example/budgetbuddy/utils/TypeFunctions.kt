package com.example.budgetbuddy.utils

import android.location.Location
import android.util.Log
import androidx.room.TypeConverter
import com.example.budgetbuddy.Data.Enumeration.TipoGasto
import com.example.budgetbuddy.Data.Enumeration.obtenerTipoDeNombre
import com.example.budgetbuddy.Data.Enumeration.obtenerTipoEnIdioma
import com.example.budgetbuddy.Data.Remote.PostGasto
import com.example.budgetbuddy.Data.Room.Gasto
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
        Log.d("ERROR DE LOCALIZACION", "LOC: ${pgasto.location} NO EXISTE!!!!!!!!!!1")
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
    Log.d("ERROR DE LOCALIZACION", "LOC: ${toLocation(pgasto.location)} CONVERSION!!!!!!!!!1")
    return Gasto(
        nombre = pgasto.nombre,
        cantidad = pgasto.cantidad.toDouble(),
        tipo = tipo,
        fecha = pgasto.fecha.toLong().toLocalDate(),
        location = toLocation(pgasto.location),
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
        location = fromLocation(gasto.location),
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
