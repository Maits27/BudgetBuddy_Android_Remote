package com.example.budgetbuddy

import android.app.Activity
import android.content.Intent
import android.health.connect.datatypes.ExerciseRoute
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.budgetbuddy.Data.Room.AuthUser
import com.example.budgetbuddy.UserVerification.correctEmail
import com.example.budgetbuddy.VM.AppViewModel
import com.example.budgetbuddy.VM.PreferencesViewModel
import com.example.budgetbuddy.VM.UserViewModel
import com.example.budgetbuddy.navigation.AppScreens
import com.example.budgetbuddy.screens.App
import com.example.budgetbuddy.screens.LoginPage
import com.example.budgetbuddy.ui.theme.BudgetBuddyTheme
import com.example.budgetbuddy.widgets.Widget
import com.example.budgetbuddy2.screens.MainView
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.coroutineScope
import java.time.LocalDate


@Composable
fun MyApp(
    userViewModel: UserViewModel,
    appViewModel: AppViewModel,
    preferencesViewModel: PreferencesViewModel,
    fusedLocationClient: FusedLocationProviderClient,
    pickMedia: ActivityResultLauncher<PickVisualMediaRequest>,
    guardarFichero: (LocalDate, String)-> Boolean) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppScreens.LoginPage.route
    ) {
        composable(AppScreens.LoginPage.route) {
            LoginPage(navController, userViewModel){ user, download ->
                Log.d("COMPARE USERS", user.toString())
                appViewModel.currentUser = user.email
                userViewModel.currentUser = user
                preferencesViewModel.changeUser(user.email)
                userViewModel.getProfileImage(user.email)
                if (download==true){
                    appViewModel.download_user_data()
                }
                userViewModel.updateLastLoggedUsername(user.email)
            }
        }
        composable(AppScreens.App.route) {
            BudgetBuddyTheme(
                user = appViewModel.currentUser,
                preferencesViewModel = preferencesViewModel
            ) {
                App(
                    navControllerMain = navController,
                    userViewModel = userViewModel,
                    appViewModel = appViewModel,
                    fusedLocationClient = fusedLocationClient,
                    pickMedia = pickMedia,
                    preferencesViewModel = preferencesViewModel,
                    guardarFichero
                )
            }
        }
    }
}

