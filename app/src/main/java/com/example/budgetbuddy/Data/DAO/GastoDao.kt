package com.example.budgetbuddy.Data.DAO

import android.util.Log
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.budgetbuddy.Data.Enumeration.TipoGasto
import com.example.budgetbuddy.Data.Room.Gasto
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate


/**
 * La DAO define la API de acceso a la base de datos de Room,
 * sin necesidad de escribir las consultas SQL (salvo los query).
 * */
@Dao
interface GastoDao {

    /////////////// Funciones Insert ///////////////

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGasto(gasto: Gasto)

    suspend fun insertGastos(gastos: List<Gasto>) = gastos.map {
        insertGasto(it)
    }

    /////////////// Funciones Delete ///////////////
    @Delete
    suspend fun deleteGasto(gasto: Gasto): Int

    /////////////// Funciones Select ///////////////
    @Transaction
    @Query("SELECT * FROM Gasto WHERE userId=:userId ORDER BY fecha")
    fun todosLosGastos(userId: String): Flow<List<Gasto>>
    @Transaction
    @Query("SELECT * FROM Gasto WHERE fecha=:fecha AND userId=:userId")
    fun elementosFecha(fecha: LocalDate, userId: String): Flow<List<Gasto>>


    /////////////// Funciones de cálculo ///////////////
    /**
     * En caso de no existir los datos que se están buscando,
     * con la función de [IFNULL] se define el valor por defecto
     * al devolver, evitando así errores de SQL Exception.
     */

    @Transaction
    @Query("SELECT ROUND(IFNULL(SUM(cantidad), 0.0), 2) FROM Gasto WHERE userId=:userId")
    fun gastoTotal(userId: String): Flow<Double>
    @Transaction
    @Query("SELECT ROUND(IFNULL(SUM(cantidad), 0.0), 2) FROM Gasto WHERE fecha=:fecha AND userId=:userId")
    fun gastoTotalDia(fecha: LocalDate, userId: String): Flow<Double>
    @Transaction
    @Query("SELECT IFNULL(COUNT(*), 0) FROM Gasto WHERE userId=:userId")
    fun cuantosGastos(userId: String): Int
    @Transaction
    @Query("SELECT IFNULL(COUNT(*), 0) FROM Gasto WHERE fecha=:fecha AND userId=:userId")
    fun cuantosDeDia(fecha: LocalDate, userId: String): Int
    @Transaction
    @Query("SELECT IFNULL(COUNT(*), 0) FROM Gasto WHERE tipo=:tipo AND userId=:userId")
    fun cuantosDeTipo(tipo: TipoGasto, userId: String): Int

    /////////////// Funciones Update ///////////////
    @Update
    fun editarGasto(gasto: Gasto): Int
}
