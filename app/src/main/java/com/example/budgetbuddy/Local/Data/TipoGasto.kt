package com.example.budgetbuddy.Local.Data

import com.example.budgetbuddy.R

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
/**
 * En la base de datos remota los [TipoGasto] se guardan por su nombre en formato
 * [String] en su versión en castellano. Para hacer la conversión a la contra y
 * guardar su tipo en la base de datos de ROOM se utiliza el siguiente método.
 */
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

/**
 * Se relaciona cada nombre de [TipoGasto] en castellano (por la base de datos remota) con un icono.
 */
fun textoAIcono(texto:String): Int{
    when{
        texto == "Comida" -> return R.drawable.food
        texto == "Hogar" -> return R.drawable.home
        texto == "Ropa" -> return R.drawable.cloth
        texto == "Actividad" -> return R.drawable.activity
        texto == "Transporte" -> return R.drawable.transport
        else -> return R.drawable.bill
    }
}