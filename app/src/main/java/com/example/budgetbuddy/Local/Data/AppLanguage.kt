package com.example.budgetbuddy.Local.Data

/*******************************************************************
 **       Clase enumerando los idiomas posibles de la APP         **
 ******************************************************************/

/**
 * Cada [AppLanguage] se almacena con un valor [language] o [code] que se
 * llamarán en base de si se requiere la codificación del idioma o su nombre.
 */
enum class AppLanguage(val language: String, val code: String) {
    EN("English", "en"),
    EU("Euskera", "eu"),
    ES("Español", "es");


    companion object {
        /**
         * Obtener [AppLanguage] De un código.
         * @code puede ser: 'eu', 'es' o 'en'
         */
        fun getFromCode(code: String) = when (code) {
            EU.code -> EU
            EN.code -> EN
            ES.code -> ES
            else -> EN
        }
    }
}
