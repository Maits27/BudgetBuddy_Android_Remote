package com.example.budgetbuddy.Local.Data

import androidx.compose.ui.graphics.painter.Painter
import com.example.budgetbuddy.Local.Room.Gasto
import com.example.budgetbuddy.Navigation.AppScreens
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime

/*******************************************************************************
 ****               Data Class Serializables para BBDD remota               ****
 *******************************************************************************/

/**
 * Data class [AuthUser] para el usuario remoto con el nombre, email y contraseña.
 * Es el equivalente al UserCreate de la API del servidor.
 */
@Serializable
data class AuthUser(
    val nombre: String = "",
    val email: String,
    val password: String = "",
)

/**
 * Data class [PostGasto] para los gastos en remoto.
 * Es el equivalente a la clase [Gasto] pero con el tipo de datos que puede soportar
 * la BBDD de Postgress (Float en vez de Double, etc.).
 */
@Serializable
data class PostGasto(
    @SerialName("nombre") val nombre: String,
    @SerialName("cantidad") val cantidad: Float,
    @SerialName("fecha") val fecha: Int,
    @SerialName("tipo") val tipo: String,
    @SerialName("latitud") val latitud: Float,
    @SerialName("longitud") val longitud: Float,
    @SerialName("user_id") val user_id: String,
    @SerialName("id") val id: String
)

/**
 * Data class [CompactGasto] para los gastos visualizados en el Widget.
 * Proporciona solo la información necesaria.
 */
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

/*******************************************************************
 **       Data Class para definir nuevos tipos de datos          **
 ******************************************************************/

/**
 * Al estar organizada de forma temporal, la APP utilizará por defecto la clase
 * [GastoDia] con los datos necesarios que se recojan de [Gasto] (de forma que
 * no se tenga acceso a toda la información en todas partes). Lo mismo para [GastoTipo]
 * que se utilizará en el composable [Dashboards] únicamente.
 *
 * El DataClass [Diseño] vale para definir el diseño de la barra de navegación, sin
 * tener que definir los botones uno a uno.
 *
 * [AlarmItem] pasa al [AlarmManager] los datos necesarios para generar la alarma.
 */


data class GastoDia(val cantidad: Double, val fecha: LocalDate)
data class GastoTipo(val cantidad: Double, val tipo: TipoGasto)
data class Diseño(val pantalla: AppScreens, val icono: Painter, val nombre: String="")

data class AlarmItem (
    val time: LocalDateTime,
    val title: String,
    val body: String
)