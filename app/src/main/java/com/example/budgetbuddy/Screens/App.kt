package com.example.budgetbuddy.Screens

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.budgetbuddy.Local.Data.AppLanguage
import com.example.budgetbuddy.Local.Data.Diseño
import com.example.budgetbuddy.R
import com.example.budgetbuddy.VM.AppViewModel
import com.example.budgetbuddy.VM.PreferencesViewModel
import com.example.budgetbuddy.VM.UserViewModel
import com.example.budgetbuddy.Navigation.AppScreens
import com.example.budgetbuddy.Navigation.navegar_a
import com.example.budgetbuddy.Screens.MenuScreens.Infor
import com.example.budgetbuddy.Screens.MenuScreens.Preferences
import com.example.budgetbuddy.Screens.MenuScreens.UserEdit
import com.example.budgetbuddy.Shared.Loading
import com.example.budgetbuddy.Shared.Perfil
import com.example.budgetbuddy.utils.CalendarWritePermission
import com.example.budgetbuddy2.screens.MainView
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.launch
import java.time.LocalDate

/***************************************************
 ***        Pantalla General de la App           ***
 ***************************************************/
/**
Este composable forma la pantalla general de la aplicación con el menú lateral

Se le pasan los parámetros de:
 * @navControllerMain:      [NavController] entre esta pantalla y el [Login].
 * @userViewModel:          ViewModel relativo a los usuarios.
 * @appViewModel:           ViewModel general de la aplicación con los métodos necesarios para editar la fecha.
 * @fusedLocationClient:    Cliente para la localización actual.
 * @pickMedia:              Cliente para selección de imágenes en la galería.
 * @preferencesViewModel:   ViewModel relativo a las preferencias de los usuarios.
 * @guardarFichero:         Función de descarga de ficheros.
 */
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
    val users by userViewModel.todosLosUsuarios.collectAsState(initial = emptyList())
    // icons to mimic drawer destinations
    val items = listOf(
        Diseño(AppScreens.UserEdit, painterResource(id = R.drawable.user), stringResource(id = R.string.edit)),
        Diseño(AppScreens.Preferences, painterResource(id = R.drawable.baseline_translate_24), stringResource(id = R.string.preferences)),
        Diseño(AppScreens.Infor, painterResource(id = R.drawable.infor), stringResource(id = R.string.infor)),
        Diseño(AppScreens.Loading, painterResource(id = R.drawable.cloud_upload), stringResource(id = R.string.subir_nube)),
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
                    modifier = colunMod.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
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
                    Loading(users, appViewModel){
                        if (logout) {
                            userViewModel.logout(context = context)
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