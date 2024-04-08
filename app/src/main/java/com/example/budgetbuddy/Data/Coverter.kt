package com.example.budgetbuddy.Data

import androidx.room.TypeConverter
import java.time.LocalDate
import android.location.Location
import android.util.Log
import com.google.gson.Gson
/**
 * Type converter para ROOM database
 *
 * Estos métodos se definen para que Room pueda interpretar y almacenar datos de
 * tipos que SQLite no conoce, transformándolos a tipos que si y al revés.
 *
 * Al importarlos en la clase de [Database], ROOM sabe cuál debe usar en cada caso.
 *
 * En este caso solo ha sido necesario definirlo para  el tipo de [LocalDate],
 * convirtiéndolo en un tipo [Long].
 */
class Converters{
    @TypeConverter
    fun fromLocalDate(value: LocalDate?): Long? {
        return value?.toEpochDay()
    }

    @TypeConverter
    fun toLocalDate(value: Long?): LocalDate? {
        return value?.let { LocalDate.ofEpochDay(it) }
    }

//    @TypeConverter
//    fun fromLocation(location: Location?): String {
//        Log.d("CONVERTER", "1. ENTRA: ${location.toString()}")
//        val gson = Gson()
//        Log.d("CONVERTER", "1. SALE: ${location?.let { gson.toJson(it) }?:""}")
//        return location?.let { gson.toJson(it) }?:""
//    }
//
//    @TypeConverter
//    fun toLocation(locationString: String?): Location? {
//        Log.d("CONVERTER", "2. ENTRA: ${locationString}")
//        val gson = Gson()
//        Log.d("CONVERTER", "2. SALE: ${locationString?.let { gson.fromJson(it, Location::class.java) }}")
//        if (locationString=="") return null
//        return locationString?.let { gson.fromJson(it, Location::class.java) }
//    }
}