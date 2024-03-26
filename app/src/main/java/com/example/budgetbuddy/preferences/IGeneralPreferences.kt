package com.example.budgetbuddy.preferences

import kotlinx.coroutines.flow.Flow


// Interfaz de acceso a las preferencias del usuario en el Datastore
interface IGeneralPreferences {
    fun language(): Flow<String>
    suspend fun setLanguage(code: String)

    fun getThemePreference(): Flow<Int>

    suspend fun saveThemePreference(theme: Int)

}