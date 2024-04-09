package com.example.budgetbuddy.screens

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.budgetbuddy.Data.Room.AuthUser
import com.example.budgetbuddy.R
import com.example.budgetbuddy.UserVerification.correctEmail
import com.example.budgetbuddy.UserVerification.correctName
import com.example.budgetbuddy.UserVerification.correctPasswd
import com.example.budgetbuddy.VM.UserViewModel
import com.example.budgetbuddy.navigation.AppScreens
import com.example.budgetbuddy.shared.ErrorText
import com.example.budgetbuddy.shared.Subtitulo
import com.example.budgetbuddy.shared.Titulo
import com.example.budgetbuddy.ui.theme.grisClaro
import com.example.budgetbuddy.ui.theme.grisOscuro
import com.example.budgetbuddy.ui.theme.verde1
import com.example.budgetbuddy.ui.theme.verde2
import com.example.budgetbuddy.ui.theme.verde3
import com.example.budgetbuddy.ui.theme.verde5
import com.example.budgetbuddy.ui.theme.verde6
import com.example.budgetbuddy.ui.theme.verdeClaro
import com.example.budgetbuddy.ui.theme.verdeOscuro
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun LoginPage(
    navController: NavController,
    userViewModel: UserViewModel,
    onCorrectLogIn: (AuthUser, Any) -> Unit
){
    val isVertical = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
    if (isVertical){
        var login by rememberSaveable {mutableStateOf(true)}
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Titulo(true)
            if (login){
                Login(
                    navController,
                    userViewModel,
                    onCorrectLogIn,
                    Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                )
            }else{
                Register(
                    navController,
                    userViewModel,
                    onCorrectLogIn,
                    Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                )
            }
            Divider(color = Color.DarkGray)
            Row {
                TextButton(
                    modifier = Modifier.padding(16.dp),
                    onClick = {login = true}) {
                    Text(text = "Login", color = MaterialTheme.colors.onBackground)
                }
                TextButton(
                    modifier = Modifier.padding(16.dp),
                    onClick = {login = false}) {
                    Text(text = "Register", color = MaterialTheme.colors.onBackground)
                }
            }
        }
    }else{
        Column (
            modifier = Modifier.verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Titulo(true)
            Row(
                Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Center
            ) {
                Login(
                    navController,
                    userViewModel,
                    onCorrectLogIn,
                    Modifier
                        .padding(end = 8.dp)
                        .weight(1f)
                )
                Register(
                    navController,
                    userViewModel,
                    onCorrectLogIn,
                    Modifier
                        .padding(start = 8.dp)
                        .weight(1f)
                )
            }
        }
    }
}

@Composable
fun Login(
    navController: NavController,
    userViewModel: UserViewModel,
    onCorrectLogIn: (AuthUser, Any) -> Unit,
    modifier: Modifier
){
    val coroutineScope = rememberCoroutineScope()

    var checked by rememberSaveable {mutableStateOf(false)}
    var correo by rememberSaveable { mutableStateOf("") }
    var passwd by rememberSaveable { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    var serverError by remember { mutableStateOf(false) }
    Column (
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Subtitulo(mensaje = "User LogIn", true)
        TextField(
            value = correo, 
            onValueChange = {correo = it}, 
            label = { Text("Email:") },
            modifier = Modifier.padding(10.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        TextField(
            value = passwd,
            onValueChange = { passwd = it },
            label = { Text("Password:") },
            modifier = Modifier.padding(10.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation() // Esta línea oculta el texto
        )
        
        if(error){
            ErrorText(text = "Incorrect email or password")
        }else if (serverError){
            ErrorText(text = "Server disconnected. Please try again later.")
        }

        Button(
            modifier = Modifier.padding(10.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = verdeOscuro,
                contentColor = grisClaro,
                disabledBackgroundColor = grisClaro,
                disabledContentColor = verdeOscuro,
            ),
            onClick = {
                coroutineScope.launch(Dispatchers.IO) {
                    val result = userViewModel.correctLogIn(correo, passwd)
                    val nombre = result["nombre"].toString()
                    serverError = if((result["runtime"] ?: false) == true){true}else{false}

                    if(!serverError){
                        withContext(Dispatchers.Main) {
                            if(nombre!=""){

                                onCorrectLogIn(AuthUser(nombre, correo, passwd), result["bajar_datos"]?:false)
                                userViewModel.getProfileImage(correo)

                                if (!checked){
                                    correo = ""
                                    passwd = ""
                                }

                                navController.navigate(AppScreens.App.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }

                            }else{
                                error = true
                            }
                        }
                    }
                }
            }) {
            Text(text = "Sign Up",
                Modifier.background(color = verdeOscuro),
                color= verdeClaro
            )
        }
    }
}

@Composable
fun Register(
    navController: NavController,
    userViewModel: UserViewModel,
    onCorrectLogIn: (AuthUser, Any) -> Unit,
    modifier: Modifier
){
    val coroutineScope = rememberCoroutineScope()

    var serverOk by remember { mutableStateOf(true) }
    var nombreOk by remember { mutableStateOf(true) }
    var emailOk by remember { mutableStateOf(true) }
    var passwdOk by remember { mutableStateOf(true) }

    var nombre by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var passwd by rememberSaveable { mutableStateOf("") }
    var passwd2 by rememberSaveable { mutableStateOf("") }

    Column (
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Subtitulo(mensaje = "User register", true)

        TextField(
            value = nombre, 
            onValueChange = {nombre = it}, 
            label = { Text("Name:") },
            modifier = Modifier.padding(10.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )
        if (!nombreOk){
            ErrorText(text = "Incorrect name.")
        }

        TextField(
            value = correo, 
            onValueChange = {correo = it}, 
            label = { Text("Email:") },
            modifier = Modifier.padding(10.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        if (!emailOk){
            ErrorText(text = "Incorrect email.")
        }
        TextField(
            value = passwd, 
            onValueChange = {passwd = it}, 
            label = { Text("Password:") },
            modifier = Modifier.padding(10.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation() // Esta línea oculta el texto
        )
        TextField(
            value = passwd2, 
            onValueChange = {passwd2 = it}, 
            label = { Text("Repeat password:") },
            modifier = Modifier.padding(10.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation() // Esta línea oculta el texto
        )
        if (!passwdOk){
            ErrorText(text = "Invalid password (the two of them must be the same).")
        }
        if (!serverOk){
            ErrorText(text = "Server disconnected. Please try again later.")
        }
        Button(
            modifier = Modifier.padding(10.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = verdeOscuro,
                contentColor = grisClaro,
                disabledBackgroundColor = grisClaro,
                disabledContentColor = verdeOscuro,
            ),
            onClick = {
                coroutineScope.launch(Dispatchers.IO) {
                    // Ejecuta el código que puede bloquear el hilo principal aquí
                    val registroExitoso = userViewModel.correctRegister(nombre, correo, passwd, passwd2)

                    // Cambiar al hilo principal para actualizar la UI
                    if (registroExitoso.values.all { it }) {
                        withContext(Dispatchers.Main) {

                            onCorrectLogIn(AuthUser(nombre, correo, passwd), false)
                            navController.navigate(AppScreens.App.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }

                        }
                    } else {
                        serverOk = registroExitoso["server"]?:true
                        nombreOk = registroExitoso["name"]?:true
                        emailOk = registroExitoso["email"]?:true
                        passwdOk = registroExitoso["password"]?:true
                    }
                }
            }
        ) {
            Text(text = "Register",
                Modifier.background(color = verdeOscuro),
                color= verdeClaro)
        }

    }
}
