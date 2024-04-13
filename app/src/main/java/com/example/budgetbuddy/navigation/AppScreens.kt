package com.example.budgetbuddy.navigation

import androidx.navigation.NavController

/**
 * Un objeto sellado, en Kotlin, es una estructura de datos que permite definir un conjunto fijo
 * y limitado de subtipos (clases, objetos o interfaces). Es un conjunto cerrado de opciones que
 * garantiza que ninguna otra clase pueda extender o implementar el conjunto definido.
 *
 * En este caso se utiliza para definir las pantallas de la aplicación
 */

sealed class AppScreens (val route: String) {
    object Loading: AppScreens("Loading")
    object App: AppScreens("App")
    object Home: AppScreens("Home")
    object Add: AppScreens("Add")
    object Edit: AppScreens("Edit")
    object Dashboards: AppScreens("Dashboards")
    object Facturas: AppScreens("Facturas")

    object LoginPage: AppScreens("LoginPage")
    object MainView: AppScreens("MainView")

    object Infor: AppScreens("Infor")
    object Preferences: AppScreens("Preferences")
    object UserEdit: AppScreens("UserEdit")
}

fun navegar_a(navController: NavController, ruta: String){
    navController.navigate(ruta) {
        popUpTo(navController.graph.startDestinationId) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}