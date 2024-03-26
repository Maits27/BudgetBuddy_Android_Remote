package com.example.budgetbuddy.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


/**************************************************************************
 ****                Preferencias del usuario en la APP                ****
 **************************************************************************/


/*************************************************
 **                  Data Store                 **
 *************************************************/

/**             (Requisito opcional)           **/
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "PREFERENCES_SETTINGS")

@Singleton
class PreferencesRepository @Inject constructor(private val context: Context) : IGeneralPreferences {
    val PREFERENCE_LANGUAGE = stringPreferencesKey("preference_lang")
    val PREFERENCE_THEME_DARK = intPreferencesKey("preference_theme")


    //////////////// Preferencias de idioma ////////////////

    /**
     * Recoge el primer valor del Flow del Datastore en los idiomas y lo devuelve.
     * Por defecto se escoge el idioma local del dispositivo Android.
     */
    override fun language(): Flow<String> = context.dataStore.data.map {
        preferences -> preferences[PREFERENCE_LANGUAGE]?: Locale.getDefault().language
    }
    override suspend fun setLanguage(code: String) {
        context.dataStore.edit { settings ->  settings[PREFERENCE_LANGUAGE]=code}
    }

    //////////////// Preferencias del tema ////////////////

    /**
     * Recoge el primer valor del Flow del Datastore en los temas y lo devuelve
     * Valor numérico del 0 al 2:
     *      0 -> Verde (por defecto)
     *      1 -> Azul
     *      2 -> Morado
     */

    override fun getThemePreference(): Flow<Int> = context.dataStore.data.map {
            preferences -> preferences[PREFERENCE_THEME_DARK]?: 0
    }

    override suspend fun saveThemePreference(theme: Int) {
        context.dataStore.edit { preferences ->
            preferences[PREFERENCE_THEME_DARK] = theme
        }
    }



}