package com.example.budgetbuddy.preferences

import kotlinx.coroutines.flow.Flow


// Interfaz de acceso a las preferencias del usuario en el Datastore
interface IGeneralPreferences {
    fun language(email: String): Flow<String>
    suspend fun setLanguage(email: String, code: String)

    fun getThemePreference(email: String): Flow<Int>
    suspend fun saveThemePreference(email: String, theme: Int)

    fun getSaveOnCalendar(email: String): Flow<Boolean>
    suspend fun changeSaveOnCalendar(email: String)
    fun getSaveLocation(email: String): Flow<Boolean>
    suspend fun changeSaveLocation(email: String)
}