package com.example.budgetbuddy.Local.Room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.budgetbuddy.Local.Converters
import com.example.budgetbuddy.Local.DAO.GastoDao
import com.example.budgetbuddy.Local.DAO.UserDao

/**
 *
 * Definición de la clase de la base de datos [Database] de ROOM.
 * (Se instancia mediante Hilt)
 * Version: 1
 *
 * Dos entidades: [Gasto] y [User]
 * Dos DAOs para dichas entidades: [GastoDao] y [UserDao]
 *
 */
@Database(entities = [User::class, Gasto::class], version = 1)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun gastoDao(): GastoDao
}

