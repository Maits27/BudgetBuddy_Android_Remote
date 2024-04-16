package com.example.budgetbuddy.Local.Data

/*******************************************************************
 **       Clase enumerando los temas de colores posibles          **
 ******************************************************************/

/**
 * Cada [Tema] se almacena con un valor [tema] que haría las veces de
 * nombre o identificador. De esta forma no es necesario definir una lista de
 * opciones, cada vez que se necesite de los tres valores de [Tema].
 */
enum class Tema(val tema: String) {
    Verde("Verde"),
    Azul("Azul"),
    Morado("Morado")
}

/**
 * Al ser una aplicación en varios idiomas, la función de abajo se encarga de, dado
 * el [code] del idioma de las preferencias, se traduce el [Tema] al idioma correspondiente.
 * (Utilizado a la hora de visualizarlo)
 */
fun obtenerTema(tipo: Tema, idioma: String): String {
    return when (idioma) {
        "eu" -> when (tipo) {   // Euskera
            Tema.Azul -> "Urdina"
            Tema.Verde -> "Orlegia"
            Tema.Morado -> "Morea"
        }
        "en" -> when (tipo) {   // Inglés
            Tema.Azul -> "Blue"
            Tema.Verde -> "Green"
            Tema.Morado -> "Purple"
        }
        else -> tipo.tema       // Por defecto, devolver el mensaje original (Castellano)
    }
}