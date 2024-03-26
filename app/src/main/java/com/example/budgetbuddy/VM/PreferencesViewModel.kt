package com.example.budgetbuddy.VM

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetbuddy.Data.Enumeration.AppLanguage
import com.example.budgetbuddy.preferences.IGeneralPreferences
import com.example.budgetbuddy.utils.LanguageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
) : ViewModel() {

    /*************************************************
     **                    Estados                  **
     *************************************************/

    val currentSetLang by languageManager::currentLang

    val idioma = preferencesRepository.language().map { AppLanguage.getFromCode(it) }

    val theme = preferencesRepository.getThemePreference()


    /*************************************************
     **                    Eventos                  **
     *************************************************/


    ////////////////////// Idioma //////////////////////

    // Cambio del idioma de preferencia
    fun changeLang(idioma: AppLanguage) {
        languageManager.changeLang(idioma)
        viewModelScope.launch(Dispatchers.IO) { preferencesRepository.setLanguage(idioma.code) }
    }

    ////////////////////// Tema //////////////////////

    // Cambio del tema de preferencia
    fun changeTheme(color: Int){
        viewModelScope.launch(Dispatchers.IO) { preferencesRepository.saveThemePreference(color) }
    }

    fun restartLang(idioma: AppLanguage){
        languageManager.changeLang(idioma)
    }


}
