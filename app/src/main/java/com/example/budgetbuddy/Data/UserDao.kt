package com.example.budgetbuddy.Data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    /////////////// Funciones Insert ///////////////

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsuario(user: User)


    /////////////// Funciones Delete ///////////////
    @Delete
    suspend fun deleteUsuario(user: User): Int

    /////////////// Funciones Select ///////////////
    @Transaction
    @Query("SELECT * FROM User")
    fun todosLosUsuarios(): Flow<List<User>>
    @Transaction
    @Query("SELECT nombre FROM User WHERE email=:email AND password=:passwd")
    fun usernamePassword(email: String, passwd:String): String?
    @Transaction
    @Query("SELECT nombre FROM User WHERE email=:email")
    fun userName(email: String): String?

    @Transaction
    @Query("SELECT * FROM User WHERE email=:email")
    fun existe(email: String): Boolean

    /////////////// Funciones Update ///////////////
    @Update
    fun editarUsuario(user: User): Int
}
