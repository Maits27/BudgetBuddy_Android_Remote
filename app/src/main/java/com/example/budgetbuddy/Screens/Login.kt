package com.example.budgetbuddy.Screens

import android.content.res.Configuration
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.budgetbuddy.Local.Data.AuthUser
import com.example.budgetbuddy.VM.AppViewModel
import com.example.budgetbuddy.VM.UserViewModel
import com.example.budgetbuddy.Navigation.AppScreens
import com.example.budgetbuddy.Navigation.navegar_a
import com.example.budgetbuddy.Shared.ErrorText
import com.example.budgetbuddy.Shared.Subtitulo
import com.example.budgetbuddy.Shared.Titulo
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
    userViewModel: UserViewModel,
    onCorrectLogIn: (AuthUser, Any) -> Unit
){
    val isVertical = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
    if (isVertical){
        /**     VERTICAL    **/
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
            /**     Elección de pantalla    **/
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
        /**     HORIZONTAL    **/
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
/***************************************************************
 ***                      ZONA DE LOGIN                      ***
 ***************************************************************/
/**
 * Login del usuario.
 *
 * Se le pasan los parámetros de:
 * @navController:  [NavController] entre esta pantalla y la [App].
 * @userViewModel:      ViewModel relativo a los usuarios.
 * @onCorrectLogIn:     Función en caso de registro correcto.
 */
@Composable
fun Login(
    navController: NavController,
    userViewModel: UserViewModel,
    onCorrectLogIn: (AuthUser, Any) -> Unit,
    modifier: Modifier
){
    val coroutineScope = rememberCoroutineScope()

    /*******************************************************************
     **                     Valores del formulario                    **
     * (rememberSaveable para no perder datos en caso de interrupción) *
     ******************************************************************/
    var correo by rememberSaveable { mutableStateOf("") }
    var passwd by rememberSaveable { mutableStateOf("") }

    /**    Parámetros para el control de los estados de los composables  **/
    var error by remember { mutableStateOf(false) }
    var logerror by remember { mutableStateOf(false) }
    var serverError by remember { mutableStateOf(false) }

    Column (
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Subtitulo(mensaje = "User LogIn", true)

        //////////////////// Email ////////////////////

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

        //////////////////// Contraseña ////////////////////

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

        //////////////////// Errores ////////////////////

        if(error){
            ErrorText(text = "Incorrect email or password")
        }else if (logerror){
            ErrorText(text = "You are logged in in other phone.\n" +
                    "Please LogOut before LogIn.")

        }else if (serverError){
            ErrorText(text = "Server disconnected. Please try again later.")
        }

        //////////////////// Aceptar ////////////////////
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
                    val nombre = if(user is AuthUser) {user.nombre}else{""}
                    serverError = if((result["runtime"] ?: false) == true){true}else{false}

                    if(!serverError){
                        val logged = userViewModel.isLogged(correo)?: false
                        withContext(Dispatchers.Main) {
                            if(nombre!=""){
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
    }
}


/******************************************************************
 ***                      ZONA DE REGISTRO                      ***
 ******************************************************************/

/**
 * Registro del usuario.
 *
 * Se le pasan los parámetros de:
 * @navController:  [NavController] entre esta pantalla y la [App].
 * @userViewModel:      ViewModel relativo a los usuarios.
 * @onCorrectLogIn:     Función en caso de registro correcto.
 */
@Composable
fun Register(
    navController: NavController,
    userViewModel: UserViewModel,
    onCorrectLogIn: (AuthUser, Any) -> Unit,
    modifier: Modifier
){
    val coroutineScope = rememberCoroutineScope()

    /**    Parámetros para el control de los estados de los composables  **/
    var serverOk by remember { mutableStateOf(true) }
    var nombreOk by remember { mutableStateOf(true) }
    var emailOk by remember { mutableStateOf(true) }
    var passwdOk by remember { mutableStateOf(true) }
    var notexist by remember { mutableStateOf(true) }

    /*******************************************************************
     **                     Valores del formulario                    **
     * (rememberSaveable para no perder datos en caso de interrupción) *
     ******************************************************************/
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

        //////////////////// Nombre ////////////////////
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

        //////////////////// Email ////////////////////
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

        //////////////////// Contraseñas ////////////////////
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

        //////////////////// Errores ////////////////////
        if (!passwdOk){
            ErrorText(text = "Invalid password (the two of them must be the same).")
        }
        if (!serverOk){
            ErrorText(text = "Server disconnected. Please try again later.")
        }
        if (!notexist){
            ErrorText(text = "The user already exist.")
        }

        //////////////////// Aceptar ////////////////////
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
