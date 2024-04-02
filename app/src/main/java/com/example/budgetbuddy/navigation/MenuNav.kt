package com.example.budgetbuddy.navigation

import android.icu.text.IDNA.Info
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.budgetbuddy.Data.Enumeration.AppLanguage
import com.example.budgetbuddy.VM.AppViewModel
import com.example.budgetbuddy.VM.PreferencesViewModel
import com.example.budgetbuddy.VM.UserViewModel
import com.example.budgetbuddy.screens.LoginPage
import com.example.budgetbuddy.screens.MenuScreens.Infor
import com.example.budgetbuddy.screens.MenuScreens.Preferences
import com.example.budgetbuddy2.screens.MainView
import java.time.LocalDate

@Composable
fun MenuNav(
    appViewModel: AppViewModel,
    preferencesViewModel: PreferencesViewModel,
    guardarFichero: (LocalDate, String)-> Boolean) {

    val navController = rememberNavController()

    val idioma by preferencesViewModel.idioma.collectAsState(initial = preferencesViewModel.currentSetLang)

    val onLanguageChange:(AppLanguage)-> Unit = {
        preferencesViewModel.changeLang(it)
    }
    val onThemeChange:(Int)-> Unit = {
        preferencesViewModel.changeTheme(it)
    }


}