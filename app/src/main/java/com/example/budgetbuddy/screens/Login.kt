package com.example.budgetbuddy.screens

import android.content.res.Configuration
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
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
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.budgetbuddy.Data.Room.AuthUser
import com.example.budgetbuddy.VM.AppViewModel
import com.example.budgetbuddy.VM.UserViewModel
import com.example.budgetbuddy.navigation.AppScreens
import com.example.budgetbuddy.navigation.navegar_a
import com.example.budgetbuddy.shared.ErrorText
import com.example.budgetbuddy.shared.Subtitulo
import com.example.budgetbuddy.shared.Titulo
import com.example.budgetbuddy.ui.theme.grisClaro
import com.example.budgetbuddy.ui.theme.verde4
import com.example.budgetbuddy.ui.theme.verdeClaro
import com.example.budgetbuddy.ui.theme.verdeOscuro
import com.example.budgetbuddy.utils.hash
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/*****************************************************************************************
 ***                      FRAGMENT LOGIN EN BASE A LA ORIENTACIÓN                      ***
 *****************************************************************************************/
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun LoginPage(
    navController: NavController,
    appViewModel: AppViewModel,
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
                    appViewModel,
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
                    modifier = Modifier
                        .padding(16.dp)
                        .weight(1f),
                    onClick = {login = true},
                    colors = if(login) {
                        ButtonDefaults.textButtonColors(backgroundColor = verde4)
                    }else{
                        ButtonDefaults.textButtonColors(backgroundColor = Transparent)
                    }
                ) {
                    Text(text = "Login", color = MaterialTheme.colors.onBackground)
                }
                TextButton(
                    modifier = Modifier
                        .padding(16.dp)
                        .weight(1f),
                    onClick = {login = false},
                    colors = if(!login) {
                        ButtonDefaults.textButtonColors(backgroundColor = verde4)
                    }else{
                        ButtonDefaults.textButtonColors(backgroundColor = Transparent)
                    }
                ) {
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
                    appViewModel,
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
/***************************************************************
 ***                      ZONA DE LOGIN                      ***
 ***************************************************************/
@Composable
fun Login(
    navController: NavController,
    appViewModel: AppViewModel,
    userViewModel: UserViewModel,
    onCorrectLogIn: (AuthUser, Any) -> Unit,
    modifier: Modifier
){
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var correo by rememberSaveable { mutableStateOf("") }
    var passwd by rememberSaveable { mutableStateOf("") }

    var error by remember { mutableStateOf(false) }
    var logerror by remember { mutableStateOf(false) }
    var serverError by remember { mutableStateOf(false) }

    Column (
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Subtitulo(mensaje = "User LogIn", true)
        /**
         * Campo del EMAIL
         */
        TextField(
            value = correo, 
            onValueChange = {correo = it}, 
            label = { Text("Email:") },
            modifier = Modifier.padding(10.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = verdeOscuro,
                cursorColor = verdeOscuro,
                focusedLabelColor = verdeOscuro)
        )
        /**
         * Campo del PASSWORD
         */
        TextField(
            value = passwd,
            onValueChange = { passwd = it },
            label = { Text("Password:") },
            modifier = Modifier.padding(10.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(), // Esta línea oculta el texto
                    colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = verdeOscuro,
            cursorColor = verdeOscuro,
            focusedLabelColor = verdeOscuro)
        )
        /**
         * Errores
         */
        if(error){
            ErrorText(text = "Incorrect email or password")
        }else if (logerror){
            ErrorText(text = "You are logged in in other phone.\n" +
                    "Please LogOut before LogIn.")

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
                    val user = result["user"]
                    Log.d("LOGGED", user.toString())
                    val nombre = if(user is AuthUser) {user.nombre}else{""}
                    serverError = if((result["runtime"] ?: false) == true){true}else{false}

                    if(!serverError){
                        val logged = userViewModel.isLogged(correo)?: false
                        withContext(Dispatchers.Main) {
                            Log.d("USER LOGGED", user.toString())
                            if(nombre!=""){
                                Log.d("SET LLU", userViewModel.lastLoggedUser?:"")
                                if(!logged) {
                                    onCorrectLogIn(
                                        AuthUser(nombre, correo, passwd.hash()),
                                        true
                                    )
                                    userViewModel.getProfileImage(correo)
                                    navegar_a(navController, AppScreens.App.route)
                                }else if (userViewModel.lastLoggedUser == correo){
                                    onCorrectLogIn(
                                        AuthUser(nombre, correo, passwd.hash()),
                                        false
                                    )
                                    userViewModel.getProfileImage(correo)
                                    navegar_a(navController, AppScreens.App.route)
                                }else{
                                    logerror = true
                                    error = false
                                }
                            }else{
                                logerror = false
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
//        Spacer(modifier = Modifier.padding(16.dp))
//        var logoutGeneral by remember {mutableStateOf(false)}
//        if (logoutGeneral) LogoutGeneral ({ logoutGeneral = false }) {
//            logoutGeneral = false
//            userViewModel.logoutDeTodosLosUsuarios(context, appViewModel)
//        }
//        Button(onClick = { logoutGeneral = true }) {
//
//        }
    }
}


/******************************************************************
 ***                      ZONA DE REGISTRO                      ***
 ******************************************************************/
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
    var notexist by remember { mutableStateOf(true) }

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
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = verdeOscuro,
                cursorColor = verdeOscuro,
                focusedLabelColor = verdeOscuro)
        )
        if (!nombreOk){
            ErrorText(text = "Incorrect name.")
        }

        TextField(
            value = correo, 
            onValueChange = {correo = it}, 
            label = { Text("Email:") },
            modifier = Modifier.padding(10.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = verdeOscuro,
                cursorColor = verdeOscuro,
                focusedLabelColor = verdeOscuro)
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
            visualTransformation = PasswordVisualTransformation(),// Esta línea oculta el texto
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = verdeOscuro,
                cursorColor = verdeOscuro,
                focusedLabelColor = verdeOscuro)
        )
        TextField(
            value = passwd2, 
            onValueChange = {passwd2 = it}, 
            label = { Text("Repeat password:") },
            modifier = Modifier.padding(10.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),// Esta línea oculta el texto
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = verdeOscuro,
                cursorColor = verdeOscuro,
                focusedLabelColor = verdeOscuro)
        )
        if (!passwdOk){
            ErrorText(text = "Invalid password (the two of them must be the same).")
        }
        if (!serverOk){
            ErrorText(text = "Server disconnected. Please try again later.")
        }
        if (!notexist){
            ErrorText(text = "The user already exist.")
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
                    if (registroExitoso.values.all {
                        Log.d("LOGGED", "$it")
                        it }) {
                        withContext(Dispatchers.Main) {
                            onCorrectLogIn(AuthUser(nombre, correo, passwd.hash()), false)
                            navegar_a(navController, AppScreens.App.route)
                        }
                    } else {
                        serverOk = registroExitoso["server"]?:true
                        notexist = registroExitoso["not_exist"]?:true
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
