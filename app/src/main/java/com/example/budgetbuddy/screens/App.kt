package com.example.budgetbuddy.screens

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.budgetbuddy.Data.Enumeration.AppLanguage
import com.example.budgetbuddy.Data.Room.Diseño
import com.example.budgetbuddy.R
import com.example.budgetbuddy.VM.AppViewModel
import com.example.budgetbuddy.VM.PreferencesViewModel
import com.example.budgetbuddy.navigation.AppScreens
import com.example.budgetbuddy.screens.MenuScreens.Infor
import com.example.budgetbuddy.screens.MenuScreens.Preferences
import com.example.budgetbuddy.screens.MenuScreens.UserEdit
import com.example.budgetbuddy.shared.Perfil
import com.example.budgetbuddy2.screens.MainView
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun App(
    navControllerMain: NavController,
    appViewModel: AppViewModel,
    preferencesViewModel: PreferencesViewModel,
    guardarFichero: (LocalDate, String)-> Boolean
){
    val navControllerSecundario = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val idioma by preferencesViewModel.idioma.collectAsState(initial = preferencesViewModel.currentSetLang)

    // icons to mimic drawer destinations
    val items = listOf(
        Diseño(AppScreens.UserEdit, painterResource(id = R.drawable.user), stringResource(id = R.string.edit)),
        Diseño(AppScreens.Preferences, painterResource(id = R.drawable.baseline_translate_24), stringResource(id = R.string.preferences)),
        Diseño(AppScreens.Infor, painterResource(id = R.drawable.infor), stringResource(id = R.string.infor)),
        Diseño(AppScreens.MainView, painterResource(id = R.drawable.close), stringResource(id = R.string.ok)),
    )
    val selectedItem = remember { mutableStateOf<Diseño?>(null) }

    val onLanguageChange:(AppLanguage)-> Unit = {
        preferencesViewModel.changeLang(it)
    }
    val onThemeChange:(Int)-> Unit = {
        preferencesViewModel.changeTheme(it)
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
                    Perfil(appViewModel = appViewModel, modifier = colunMod)
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
                                navControllerSecundario.navigate(item.pantalla.route) {
                                    popUpTo(navControllerSecundario.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
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
                        onDrawerOpen = onDrawerOpen,
                        guardarFichero
                    )
                }
                composable(AppScreens.UserEdit.route) {
                    UserEdit {
                        navControllerSecundario.navigate(AppScreens.MainView.route) {
                            popUpTo(navControllerSecundario.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
                composable(AppScreens.Preferences.route) {
                    Preferences(
                        onLanguageChange = onLanguageChange,
                        idioma = idioma.code,
                        onThemeChange = onThemeChange) {
                        navControllerSecundario.navigate(AppScreens.MainView.route) {
                            popUpTo(navControllerSecundario.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
                composable(AppScreens.Infor.route) {
                    Infor{
                        navControllerSecundario.navigate(AppScreens.MainView.route) {
                            popUpTo(navControllerSecundario.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            }
        }
    )
}