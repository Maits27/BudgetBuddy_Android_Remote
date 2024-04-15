package com.example.budgetbuddy

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.health.connect.datatypes.ExerciseRoute
import android.os.Build
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.budgetbuddy.AlarmManager.AndroidAlarmScheduler
import com.example.budgetbuddy.Data.Room.AuthUser
import com.example.budgetbuddy.Data.Room.User
import com.example.budgetbuddy.UserVerification.correctEmail
import com.example.budgetbuddy.VM.AppViewModel
import com.example.budgetbuddy.VM.PreferencesViewModel
import com.example.budgetbuddy.VM.UserViewModel
import com.example.budgetbuddy.navigation.AppScreens
import com.example.budgetbuddy.screens.App
import com.example.budgetbuddy.screens.LoginPage
import com.example.budgetbuddy.shared.NoCalendar
import com.example.budgetbuddy.ui.theme.BudgetBuddyTheme
import com.example.budgetbuddy.utils.CalendarPermission
import com.example.budgetbuddy.utils.authuser_to_user
import com.example.budgetbuddy.widgets.Widget
import com.example.budgetbuddy2.screens.MainView
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.coroutineScope
import java.time.LocalDate


@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun MyApp(
    userViewModel: UserViewModel,
    appViewModel: AppViewModel,
    preferencesViewModel: PreferencesViewModel,
    fusedLocationClient: FusedLocationProviderClient,
    pickMedia: ActivityResultLauncher<PickVisualMediaRequest>,
    subscribe:()-> Unit,
    guardarFichero: (LocalDate, String)-> Boolean) {

    val navController = rememberNavController()
    val context = LocalContext.current
    val scheduler = AndroidAlarmScheduler(context)

    NavHost(
        navController = navController,
        startDestination = AppScreens.LoginPage.route
    ) {
        composable(AppScreens.LoginPage.route) {
            LoginPage(navController, appViewModel, userViewModel){ user, download ->
                Log.d("COMPARE USERS", user.toString())
                appViewModel.currentUser = user.email
                userViewModel.currentUser = user
                preferencesViewModel.changeUser(user.email)
                userViewModel.getProfileImage(user.email)
                if (download==true){
                    userViewModel.insertLocal(authuser_to_user(user))
                }
                appViewModel.download_user_data(context, scheduler)
                userViewModel.loginUser(user.email, true)
                userViewModel.updateLastLoggedUsername(user.email)
                subscribe()
            }
        }
        composable(AppScreens.App.route) {
            BudgetBuddyTheme(
                user = appViewModel.currentUser,
                preferencesViewModel = preferencesViewModel
            ) {
                val calendarPermissionState = rememberPermissionState(
                    permission = Manifest.permission.READ_CALENDAR
                )
                LaunchedEffect(true){
                    if (!calendarPermissionState.status.isGranted) {
                        calendarPermissionState.launchPermissionRequest()
                    }
                }

                if (calendarPermissionState.status.isGranted) {
                    App(
                        navControllerMain = navController,
                        userViewModel = userViewModel,
                        appViewModel = appViewModel,
                        fusedLocationClient = fusedLocationClient,
                        pickMedia = pickMedia,
                        preferencesViewModel = preferencesViewModel,
                        guardarFichero
                    )

                }else{
                    CalendarPermission()
                    NoCalendar()
                }
            }
        }
    }
}

