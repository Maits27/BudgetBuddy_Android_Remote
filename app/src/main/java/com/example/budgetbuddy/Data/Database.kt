package com.example.budgetbuddy.Data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
/**
 *
 * Definición de la clase de la base de datos [Database] de ROOM.
 * (Se instancia mediante Hilt)
 * Version: 2
 *
 * Única entidad: [Gasto]
 * Único DAO para dicha entidad: [GastoDao]
 *
 */
@Database(entities = [User::class, Gasto::class], version = 1)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun gastoDao(): GastoDao
}

