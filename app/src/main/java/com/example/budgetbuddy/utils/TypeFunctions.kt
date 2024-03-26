package com.example.budgetbuddy.utils

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