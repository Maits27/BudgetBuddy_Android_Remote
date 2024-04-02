package com.example.budgetbuddy.utils

import android.util.Log
import com.example.budgetbuddy.Data.Enumeration.TipoGasto
import com.example.budgetbuddy.Data.Enumeration.obtenerTipoDeNombre
import com.example.budgetbuddy.Data.Enumeration.obtenerTipoEnIdioma
import com.example.budgetbuddy.Data.Remote.PostGasto
import com.example.budgetbuddy.Data.Room.Gasto
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
        user_id = gasto.userId,
        id = gasto.id
        )
}

