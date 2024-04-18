package com.example.budgetbuddy

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.budgetbuddy.AlarmManager.AndroidAlarmScheduler
import com.example.budgetbuddy.Local.Data.AlarmItem
import com.example.budgetbuddy.Local.Room.User
import com.example.budgetbuddy.VM.AppViewModel
import com.example.budgetbuddy.VM.PreferencesViewModel
import com.example.budgetbuddy.VM.UserViewModel
import com.example.budgetbuddy.Navigation.AppScreens
import com.example.budgetbuddy.Screens.App
import com.example.budgetbuddy.Screens.LoginPage
import com.example.budgetbuddy.Shared.NoCalendar
import com.example.budgetbuddy.ui.theme.BudgetBuddyTheme
import com.example.budgetbuddy.utils.CalendarPermission
import com.example.budgetbuddy.utils.authuser_to_user
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime


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
    val coroutineScope = rememberCoroutineScope()
    val scheduler = AndroidAlarmScheduler(context)

    NavHost(
        navController = navController,
        startDestination = AppScreens.LoginPage.route
    ) {
        composable(AppScreens.LoginPage.route) {
            LoginPage(navController, userViewModel){ user, download ->
                appViewModel.currentUser = user.email
                userViewModel.currentUser = user
                preferencesViewModel.changeUser(user.email)
                userViewModel.getProfileImage(user.email)
                if (download==true){
                    userViewModel.insertLocal(authuser_to_user(user))
                }
                appViewModel.download_user_data(context, scheduler)
//                userViewModel.editUser(User(user.nombre, user.email, user.password, true))
                userViewModel.loginUser(user.email, true)
                userViewModel.updateLastLoggedUsername(user.email)
                subscribe()
                scheduler.schedule(
                    AlarmItem(
                        time = LocalDateTime.now().plusHours(1),
                        title = "",
                        body = ""
                    ),
                    logout = user.email
                )
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

