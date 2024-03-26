package com.example.budgetbuddy.Data

import androidx.room.TypeConverter
import java.time.LocalDate

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
}