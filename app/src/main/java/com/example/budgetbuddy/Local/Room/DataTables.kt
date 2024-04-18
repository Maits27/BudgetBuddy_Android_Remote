package com.example.budgetbuddy.Local.Room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.budgetbuddy.Local.Data.AppLanguage
import com.example.budgetbuddy.Local.Data.TipoGasto
import com.example.budgetbuddy.Local.Data.obtenerTipoEnIdioma
import java.time.LocalDate
import java.util.UUID

/*******************************************************************
 **       Data Class para la base de datos local (Room)          **
 ******************************************************************/

/**
 * Clase [User], utilizada para almacenar toda la información de todos las entidades
 * tipo [User] en la base de datos de Room.
 */
@Entity
data class User(
    var nombre: String,
    @PrimaryKey val email: String,
    var password: String,
    var login: Boolean
)


/**
 * Clase [Gasto], utilizada para almacenar toda la información de todos las entidades
 * tipo [Gasto] en la base de datos de Room.
 * La foreignKey establece la conexión entre esta entidad y la de [User]
 */
@Entity(
    foreignKeys = [
        ForeignKey(entity = User::class, parentColumns = ["email"], childColumns = ["userId"], onDelete = ForeignKey.CASCADE),
    ]
)
data class Gasto(
    var nombre: String,
    var cantidad: Double,
    var fecha: LocalDate,
    var tipo: TipoGasto,
    var latitud: Double,
    var longitud: Double,
    var userId: String,
    @PrimaryKey val id: String = UUID.randomUUID().toString()
){
    //Cada Gasto viene con esta función definida de forma que no haya que implementar la consulta
    fun toString(idioma: AppLanguage): String {
        return "${nombre} (${obtenerTipoEnIdioma(tipo, idioma.code)}):\t\t${cantidad}€\n"
    }
}

