package com.example.budgetbuddy.Data.Room

import android.location.Location
import androidx.compose.ui.graphics.painter.Painter

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.budgetbuddy.Data.Enumeration.AppLanguage
import com.example.budgetbuddy.Data.Enumeration.TipoGasto
import com.example.budgetbuddy.Data.Enumeration.obtenerTipoEnIdioma
import com.example.budgetbuddy.navigation.AppScreens
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID


/*******************************************************************
 **       Data Clase para definir nuevos tipos de datos          **
 ******************************************************************/

/**
 * Al estar organizada de forma temporal, la APP utilizará por defecto la clase
 * [GastoDia] con los datos necesarios que se recojan de [Gasto] (de forma que
 * no se tenga acceso a toda la información en todas partes). Lo mismo para [GastoTipo]
 * que se utilizará en el composable [Dashboards] únicamente.
 *
 * El DataClass [Diseño] vale para definir el diseño de la barra de navegación, sin
 * tener que definir los botones uno a uno.
 */


data class GastoDia(val cantidad: Double, val fecha: LocalDate)
data class GastoTipo(val cantidad: Double, val tipo: TipoGasto)
data class Diseño(val pantalla: AppScreens, val icono: Painter, val nombre: String="")

data class AlarmItem (
    val time: LocalDateTime,
    val title: String,
    val body: String
)

/*******************************************************************************
 ****                        User Entity in Database                        ****
 *******************************************************************************/

/**
 * Data class representing the user entity. Defined by a [username] and a [password].
 */
@Serializable
data class AuthUser(
    val nombre: String = "",
    val email: String,
    val password: String = "",
)

/**
 * Clase [Gasto], utilizada para almacenar toda la información de todos las entidades
 * tipo [Gasto] en la base de datos de Room. Si se quieren utilizar datos específicos
 * dependiendo de la pantalla (como es siempre el caso). Se recurren a las data-class
 * definidas arriba.
 */
@Entity
data class User(
    var nombre: String,
    @PrimaryKey val email: String,
    var password: String
)


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


@Serializable
data class CompactGasto(
    var nombre: String,
    var cantidad: Double,
    var tipo: TipoGasto,
){
    constructor(gasto: Gasto): this(
        nombre = gasto.nombre,
        cantidad = gasto.cantidad,
        tipo = gasto.tipo
    )
}