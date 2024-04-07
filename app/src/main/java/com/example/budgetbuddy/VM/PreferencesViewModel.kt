package com.example.budgetbuddy.VM

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetbuddy.Data.Enumeration.AppLanguage
import com.example.budgetbuddy.preferences.IGeneralPreferences
import com.example.budgetbuddy.utils.LanguageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

/********************************************************
 ****            Preferences View Model              ****
 ********************************************************/
/**
 * View Model de Hilt para preferencias del usuario local
 *
 * @preferencesRepository: implementación de [IGeneralPreferences] y repositorio a cargo de realizar los cambios en el DataStore.
 * @languageManager: Encargado del cambio de idioma en la APP.
 */
@HiltViewModel
class PreferencesViewModel @Inject constructor(
    private val preferencesRepository: IGeneralPreferences,
    private val languageManager: LanguageManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    /*************************************************
     **                    Estados                  **
     *************************************************/
    var currentUser by mutableStateOf("")

    val currentSetLang by languageManager::currentLang

    val idioma = preferencesRepository.language(currentUser).map { AppLanguage.getFromCode(it) }

    val theme = preferencesRepository.getThemePreference(currentUser)

    val saveOnCalendar = preferencesRepository.getSaveOnCalendar(currentUser)


    /*************************************************
     **                    Eventos                  **
     *************************************************/


    ////////////////////// Idioma //////////////////////

    // Cambio del idioma de preferencia
    fun changeLang(i: AppLanguage) {
        Log.d("IDIOMAS USER", currentUser)
        languageManager.changeLang(i)
        viewModelScope.launch(Dispatchers.IO) {
            preferencesRepository.setLanguage(currentUser, i.code)
        }
    }


    ////////////////////// Tema //////////////////////

    // Cambio del tema de preferencia
    fun changeTheme(color: Int){
        viewModelScope.launch(Dispatchers.IO) { preferencesRepository.saveThemePreference(currentUser, color) }
    }

    fun restartLang(i: AppLanguage){
        viewModelScope.launch {
            languageManager.changeLang(preferencesRepository.language(currentUser).map { AppLanguage.getFromCode(it) }.first())
        }
    }

    fun changeSaveOnCalendar(){
        viewModelScope.launch(Dispatchers.IO) { preferencesRepository.changeSaveOnCalendar(currentUser) }
    }


}
