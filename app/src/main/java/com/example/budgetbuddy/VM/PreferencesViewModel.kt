package com.example.budgetbuddy.VM

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetbuddy.Local.Data.AppLanguage
import com.example.budgetbuddy.Preferences.IGeneralPreferences
import com.example.budgetbuddy.utils.LanguageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
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
    private val _currentUser = MutableStateFlow("")

    val currentUser: Flow<String> = _currentUser

    val currentSetLang by languageManager::currentLang

    val idioma: (String)-> Flow<AppLanguage> = { preferencesRepository.language(it).map { AppLanguage.getFromCode(it) } }

    val theme: (String)-> Flow<Int> = { preferencesRepository.getThemePreference(it)}

    val saveOnCalendar: (String)-> Flow<Boolean> = { preferencesRepository.getSaveOnCalendar(it)}
    val saveLocation: (String)-> Flow<Boolean> = { preferencesRepository.getSaveOnCalendar(it)}


    /*************************************************
     **                    Eventos                  **
     *************************************************/

    fun changeUser(email: String){
        _currentUser.value = email
    }


    ////////////////////// Idioma //////////////////////

    // Cambio del idioma de preferencia
    fun changeLang(i: AppLanguage) {
        languageManager.changeLang(i)
        viewModelScope.launch(Dispatchers.IO) {
            preferencesRepository.setLanguage(currentUser.first(), i.code)
        }
    }


    ////////////////////// Tema //////////////////////

    // Cambio del tema de preferencia
    fun changeTheme(color: Int){
        viewModelScope.launch(Dispatchers.IO) { preferencesRepository.saveThemePreference(currentUser.first(), color) }
    }

    fun restartLang(i: AppLanguage){
        viewModelScope.launch {
            languageManager.changeLang(i)
        }
    }

    fun changeSaveOnCalendar(){
        viewModelScope.launch(Dispatchers.IO) { preferencesRepository.changeSaveOnCalendar(currentUser.first()) }
    }

    fun changeSaveLocation(){
        viewModelScope.launch(Dispatchers.IO) { preferencesRepository.changeSaveLocation(currentUser.first()) }
    }

}
