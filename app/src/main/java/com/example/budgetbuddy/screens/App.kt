package com.example.budgetbuddy.screens

import android.Manifest
import android.health.connect.datatypes.ExerciseRoute
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.updateAll
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.budgetbuddy.Data.Enumeration.AppLanguage
import com.example.budgetbuddy.Data.Room.Diseño
import com.example.budgetbuddy.Data.Room.User
import com.example.budgetbuddy.R
import com.example.budgetbuddy.VM.AppViewModel
import com.example.budgetbuddy.VM.PreferencesViewModel
import com.example.budgetbuddy.VM.UserViewModel
import com.example.budgetbuddy.navigation.AppScreens
import com.example.budgetbuddy.navigation.navegar_a
import com.example.budgetbuddy.screens.MenuScreens.Infor
import com.example.budgetbuddy.screens.MenuScreens.Preferences
import com.example.budgetbuddy.screens.MenuScreens.UserEdit
import com.example.budgetbuddy.shared.Loading
import com.example.budgetbuddy.shared.Perfil
import com.example.budgetbuddy.utils.CalendarWritePermission
import com.example.budgetbuddy.utils.user_to_authUser
import com.example.budgetbuddy.widgets.Widget
import com.example.budgetbuddy2.screens.MainView
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun App(
    navControllerMain: NavController,
    userViewModel: UserViewModel,
    appViewModel: AppViewModel,
    fusedLocationClient: FusedLocationProviderClient,
    pickMedia: ActivityResultLauncher<PickVisualMediaRequest>,
    preferencesViewModel: PreferencesViewModel,
    guardarFichero: (LocalDate, String)-> Boolean
){
    CalendarWritePermission()
    preferencesViewModel.restartLang(
        preferencesViewModel.idioma(appViewModel.currentUser).collectAsState(
            initial = preferencesViewModel.currentSetLang).value)
    val context = LocalContext.current

    var logout by rememberSaveable {
        mutableStateOf(false)
    }
    val navControllerSecundario = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val saveOnCalendar by preferencesViewModel.saveOnCalendar(appViewModel.currentUser).collectAsState(initial = true)
    val saveLocation by preferencesViewModel.saveLocation(appViewModel.currentUser).collectAsState(initial = true)
    // icons to mimic drawer destinations
    val items = listOf(
        Diseño(AppScreens.UserEdit, painterResource(id = R.drawable.user), stringResource(id = R.string.edit)),
        Diseño(AppScreens.Preferences, painterResource(id = R.drawable.baseline_translate_24), stringResource(id = R.string.preferences)),
        Diseño(AppScreens.Infor, painterResource(id = R.drawable.infor), stringResource(id = R.string.infor)),
        Diseño(AppScreens.Loading, painterResource(id = R.drawable.baseline_cloud_upload_24), stringResource(id = R.string.subir_nube)),
        Diseño(AppScreens.MainView, painterResource(id = R.drawable.close), stringResource(id = R.string.ok)),
    )
    val selectedItem = remember { mutableStateOf<Diseño?>(null) }

    val onLanguageChange:(AppLanguage)-> Unit = {
        preferencesViewModel.changeLang(it)
    }
    val onThemeChange:(Int)-> Unit = {
        preferencesViewModel.changeTheme(it)
    }
    val onEditProfile: () -> Unit = {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        scope.launch { drawerState.close() }
    }
    val onDrawerOpen: () -> Unit = {
        scope.launch { drawerState.open() }
    }


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                val colunMod = Modifier.width(300.dp)
                Column (
                    modifier = colunMod,
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Perfil(
                        appViewModel = appViewModel,
                        userViewModel = userViewModel,
                        modifier = colunMod,
                        onEditProfile = onEditProfile
                    )
                    Spacer(Modifier.height(12.dp))
                    items.forEach { item ->
                        NavigationDrawerItem(
                            icon = { Icon(item.icono, contentDescription = null) },
                            label = { Text(item.nombre) },
                            selected = item == selectedItem.value,
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                }
                                selectedItem.value = item
                                navegar_a(navControllerSecundario, item.pantalla.route)
                                selectedItem.value = null
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }
                }
            }
        },
        content = {
            NavHost(
                navController = navControllerSecundario,
                startDestination = AppScreens.MainView.route
            ) {

                composable(AppScreens.MainView.route) {
                    MainView(
                        navControllerMain = navControllerMain,
                        appViewModel = appViewModel,
                        preferencesViewModel = preferencesViewModel,
                        fusedLocationClient = fusedLocationClient,
                        onDrawerOpen = onDrawerOpen,
                        onLogout = {
                            logout = true
                            navegar_a(navControllerSecundario, AppScreens.Loading.route)
                                   },
                        guardarFichero
                    )
                }
                composable(AppScreens.UserEdit.route) {
                    UserEdit (
                        userViewModel = userViewModel,
                        currentUser = appViewModel.currentUser
                    ){
                        navegar_a(navControllerSecundario, AppScreens.MainView.route)
                    }
                }
                composable(AppScreens.Preferences.route) {
                    Preferences(
                        onLanguageChange = onLanguageChange,
                        onThemeChange = onThemeChange,
                        onSaveChange = {preferencesViewModel.changeSaveOnCalendar()},
                        onSaveLocation = {preferencesViewModel.changeSaveLocation()},
                        saveChange = saveOnCalendar,
                        saveLocation = saveLocation
                    ){
                        navegar_a(navControllerSecundario, AppScreens.MainView.route)

                    }
                }
                composable(AppScreens.Infor.route) {
                    Infor{ navegar_a(navControllerSecundario, AppScreens.MainView.route) }
                }
                composable(AppScreens.Loading.route) {
                    Loading(appViewModel){
                        if (logout) {
                            userViewModel.logout(context)
                            preferencesViewModel.changeUser("")
                            logout = false
                            navegar_a(navControllerMain, AppScreens.LoginPage.route)
                        }
                        else navegar_a(navControllerSecundario, AppScreens.MainView.route)
                    }
                }
            }
        }
    )
}