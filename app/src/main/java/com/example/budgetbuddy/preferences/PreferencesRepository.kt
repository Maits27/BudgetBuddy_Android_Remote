package com.example.budgetbuddy.preferences

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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
//    val LAST_LOGGED_USER = stringPreferencesKey("last_logged_user")
    fun PREFERENCE_LANGUAGE(email: String) = stringPreferencesKey("${email}_preference_lang")
    fun PREFERENCE_THEME_DARK(email: String) = intPreferencesKey("${email}_preference_theme")
    fun PREFERENCE_SAVE(email: String) = booleanPreferencesKey("${email}_preference_save")

//    override suspend fun getLastLoggedUser(): String? =
//        context.dataStore.data.first()[LAST_LOGGED_USER]
//
//    // Set the last logged user on DataStore Preferences
//    override suspend fun setLastLoggedUser(username: String) {
//        context.dataStore.edit { preferences ->
//            preferences[LAST_LOGGED_USER] = username
//        }
//    }

    //////////////// Preferencias de idioma ////////////////

    /**
     * Recoge el primer valor del Flow del Datastore en los idiomas y lo devuelve.
     * Por defecto se escoge el idioma local del dispositivo Android.
     */
    override fun language(email: String): Flow<String> = context.dataStore.data.map {
        preferences ->
        Log.d("IDIOMA", "Preferences to string: ${preferences.toString()}")
        Log.d("IDIOMA", "Email: $email")
        Log.d("IDIOMA", "Preferences email o comillas: ${preferences[PREFERENCE_LANGUAGE(email)]?:""}")
        Log.d("IDIOMA", "Name preferences lang email: ${PREFERENCE_LANGUAGE(email)}")
        preferences[PREFERENCE_LANGUAGE(email)]?: "en"

    }
    override suspend fun setLanguage(email: String, code: String) {
        context.dataStore.edit { settings ->
            Log.d("IDIOMA", "Preferences to string: ${settings.toString()}")
            Log.d("IDIOMA", "Email: $email")
            Log.d("IDIOMA", "Code: $code")
            Log.d("IDIOMA", "Preferences email o comillas: ${settings[PREFERENCE_LANGUAGE(email)]?:""}")
            Log.d("IDIOMA", "Name preferences lang email: ${PREFERENCE_LANGUAGE(email).name}")
            settings[PREFERENCE_LANGUAGE(email)]=code}
    }

    //////////////// Preferencias del tema ////////////////

    /**
     * Recoge el primer valor del Flow del Datastore en los temas y lo devuelve
     * Valor numérico del 0 al 2:
     *      0 -> Verde (por defecto)
     *      1 -> Azul
     *      2 -> Morado
     */

    override fun getThemePreference(email: String): Flow<Int> = context.dataStore.data.map {
            preferences -> preferences[PREFERENCE_THEME_DARK(email)]?: 0
    }

    override suspend fun saveThemePreference(email: String, theme: Int) {
        context.dataStore.edit { preferences ->
            preferences[PREFERENCE_THEME_DARK(email)] = theme
        }
    }

    override fun getSaveOnCalendar(email: String): Flow<Boolean> = context.dataStore.data.map {
            preferences -> preferences[PREFERENCE_SAVE(email)]?: true
    }

    override suspend fun changeSaveOnCalendar(email: String) {
        context.dataStore.edit { preferences ->
            preferences[PREFERENCE_SAVE(email)] = !getSaveOnCalendar(email).first()
        }
    }


}