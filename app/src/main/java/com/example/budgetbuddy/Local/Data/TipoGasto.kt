package com.example.budgetbuddy.Local.Data

/*******************************************************************
 **       Clase enumerando los tipos de gastos posibles          **
 ******************************************************************/

/**
 * Cada tipo de [Gasto] se almacena con un valor [tipo] que haría las veces de
 * nombre o identificador. De esta forma no es necesario definir una lista de
 * opciones, cada vez que se necesite de los cinco [TipoGasto].
 */


enum class TipoGasto(val tipo: String) {
    Comida("Comida"),
    Hogar("Hogar"),
    Ropa("Ropa"),
    Actividad("Actividad"),
    Transporte("Transporte"),
    Otros("Otros")
}

/**
 * Al ser una aplicación en varios idiomas, la función de abajo se encarga de, dado
 * el [code] del idioma de las preferencias, se traduce el tipo al idioma correspondiente.
 */
fun obtenerTipoEnIdioma(tipo: TipoGasto, idioma: String): String {
    return when (idioma) {
        "eu" -> when (tipo) {   // Euskera
            TipoGasto.Comida -> "Janaria"
            TipoGasto.Hogar -> "Etxea"
            TipoGasto.Ropa -> "Arropa"
            TipoGasto.Actividad -> "Jarduera"
            TipoGasto.Transporte -> "Garraioa"
            TipoGasto.Otros -> "Besteak"
        }
        "en" -> when (tipo) {   // Inglés
            TipoGasto.Comida -> "Food"
            TipoGasto.Hogar -> "Home"
            TipoGasto.Ropa -> "Clothes"
            TipoGasto.Actividad -> "Activity"
            TipoGasto.Transporte -> "Transport"
            TipoGasto.Otros -> "Others"
        }
        else -> tipo.tipo       // Por defecto, devolver el mensaje original (Castellano)
    }
}

fun obtenerTipoDeNombre(nombre: String): TipoGasto?{
    return when (nombre){
        "Comida" -> TipoGasto.Comida
        "Hogar" -> TipoGasto.Hogar
        "Ropa" -> TipoGasto.Ropa
        "Transporte" -> TipoGasto.Transporte
        "Actividad" -> TipoGasto.Actividad
        "Otros" -> TipoGasto.Otros
        else -> null
    }
}
