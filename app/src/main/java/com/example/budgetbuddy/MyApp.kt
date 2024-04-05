package com.example.budgetbuddy

import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.budgetbuddy.UserVerification.correctEmail
import com.example.budgetbuddy.VM.AppViewModel
import com.example.budgetbuddy.VM.PreferencesViewModel
import com.example.budgetbuddy.VM.UserViewModel
import com.example.budgetbuddy.navigation.AppScreens
import com.example.budgetbuddy.screens.App
import com.example.budgetbuddy.screens.LoginPage
import com.example.budgetbuddy2.screens.MainView
import kotlinx.coroutines.coroutineScope
import java.time.LocalDate


@Composable
fun MyApp(
    userViewModel: UserViewModel,
    appViewModel: AppViewModel,
    pickMedia: ActivityResultLauncher<PickVisualMediaRequest>,
    preferencesViewModel: PreferencesViewModel,
    guardarFichero: (LocalDate, String)-> Boolean) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppScreens.LoginPage.route
    ) {
        composable(AppScreens.LoginPage.route) {
            LoginPage(navController, userViewModel){ email, name, download ->
                appViewModel.currentUser = email
                userViewModel.currentUserName = name
                if (download==true){
                    appViewModel.download_user_data()
                }
            }
        }
        composable(AppScreens.App.route) {
            App(
                navControllerMain = navController,
                userViewModel = userViewModel,
                appViewModel = appViewModel,
                pickMedia = pickMedia,
                preferencesViewModel = preferencesViewModel,
                guardarFichero
            )
        }
    }
}

