package com.example.budgetbuddy.Data.Room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.budgetbuddy.Data.Converters
import com.example.budgetbuddy.Data.DAO.GastoDao
import com.example.budgetbuddy.Data.DAO.UserDao

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

