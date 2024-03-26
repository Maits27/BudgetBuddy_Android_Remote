package com.example.budgetbuddy.screens

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
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
import com.example.budgetbuddy.R
import com.example.budgetbuddy.UserVerification.correctEmail
import com.example.budgetbuddy.UserVerification.correctName
import com.example.budgetbuddy.UserVerification.correctPasswd
import com.example.budgetbuddy.VM.UserViewModel
import com.example.budgetbuddy.navigation.AppScreens
import com.example.budgetbuddy.shared.ErrorText
import com.example.budgetbuddy.ui.theme.grisClaro
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun LoginPage(
    navController: NavController,
    userViewModel: UserViewModel,
    onCorrectLogIn: (String, String) -> Unit
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
            Divider(color = grisClaro)
            Row {
                TextButton(
                    modifier = Modifier.padding(16.dp),
                    onClick = {login = true}) {
                    Text(text = "Login", color = MaterialTheme.colors.onBackground)
                }
                TextButton(
                    modifier = Modifier.padding(16.dp),
                    onClick = {login = false}) {
                    Text(text = stringResource(id = R.string.register), color = MaterialTheme.colors.onBackground)
                }
            }
        }
    }else{
        Row(
            Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
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

@Composable
fun Login(
    navController: NavController,
    userViewModel: UserViewModel,
    onCorrectLogIn: (String, String) -> Unit,
    modifier: Modifier
){
    val coroutineScope = rememberCoroutineScope()

    var checked by rememberSaveable {mutableStateOf(true)}
    var correo by rememberSaveable { mutableStateOf("") }
    var passwd by rememberSaveable { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    Column (
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(text = stringResource(id = R.string.login_head))

        Divider(color = grisClaro)
        TextField(
            value = correo, 
            onValueChange = {correo = it}, 
            label = { Text(stringResource(id = R.string.email)) },
            modifier = Modifier.padding(10.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        TextField(
            value = passwd,
            onValueChange = { passwd = it },
            label = { Text(stringResource(id = R.string.passwd)) },
            modifier = Modifier.padding(10.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation() // Esta línea oculta el texto
        )

        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            Text(text = stringResource(id = R.string.guardar_datos))
            Checkbox(
                checked = checked,
                onCheckedChange = { checked = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colors.primary, // Color when checked
                    uncheckedColor = MaterialTheme.colors.secondary // Color when unchecked
                )
            )
        }
        
        if(error){
            ErrorText(text = stringResource(id = R.string.login_error))
        }

        Button(
            modifier = Modifier.padding(10.dp),
            onClick = {
                coroutineScope.launch(Dispatchers.IO) {
                    Log.d("LOGIN", "TODO OK0")
                    val nombre = userViewModel.correctLogIn(correo, passwd)
                    Log.d("LOGIN", "TODO OK1")
                    withContext(Dispatchers.Main) {
                        if(nombre!=""){
                            onCorrectLogIn(correo, nombre)
                            Log.d("LOGIN", "TODO OK2")
                            if (!checked){
                                correo = ""
                                passwd = ""
                            }
                            navController.navigate(AppScreens.MainView.route) {
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
            }) {
            Text(text = "Login")
        }
    }
}

@Composable
fun Register(
    navController: NavController,
    userViewModel: UserViewModel,
    onCorrectLogIn: (String, String) -> Unit,
    modifier: Modifier
){
    val coroutineScope = rememberCoroutineScope()

    var nombre by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var passwd by rememberSaveable { mutableStateOf("") }
    var passwd2 by rememberSaveable { mutableStateOf("") }

    var nameError by rememberSaveable { mutableStateOf(false) }
    var emailError by rememberSaveable { mutableStateOf(false) }
    var passwdError by rememberSaveable { mutableStateOf(false) }
    Column (
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(text = stringResource(id = R.string.register_head))

        Divider(color = grisClaro)

        TextField(
            value = nombre, 
            onValueChange = {nombre = it}, 
            label = { Text(stringResource(id = R.string.name)) },
            modifier = Modifier.padding(10.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )
        if (nameError){
            ErrorText(text = stringResource(id = R.string.name_error))
        }

        TextField(
            value = correo, 
            onValueChange = {correo = it}, 
            label = { Text(stringResource(id = R.string.email)) },
            modifier = Modifier.padding(10.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        if (emailError){
            ErrorText(text = stringResource(id = R.string.email_error))
        }
        TextField(
            value = passwd, 
            onValueChange = {passwd = it}, 
            label = { Text(stringResource(id = R.string.passwd)) },
            modifier = Modifier.padding(10.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation() // Esta línea oculta el texto
        )
        TextField(
            value = passwd2, 
            onValueChange = {passwd2 = it}, 
            label = { Text(stringResource(id = R.string.passwd2)) },
            modifier = Modifier.padding(10.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation() // Esta línea oculta el texto
        )
        if (passwdError){
            ErrorText(text = stringResource(id = R.string.passwd_error))
        }

        Button(
            modifier = Modifier.padding(10.dp),
            onClick = {
                coroutineScope.launch(Dispatchers.IO) {
                    // Ejecuta el código que puede bloquear el hilo principal aquí

                    val registroExitoso = userViewModel.correctRegister(nombre, correo, passwd, passwd2)
                    // Cambiar al hilo principal para actualizar la UI
                    withContext(Dispatchers.Main) {
                        if (registroExitoso) {
                            onCorrectLogIn(correo, nombre)
                            navController.navigate(AppScreens.MainView.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        } else {
                            // Manejar el registro fallido si es necesario
                        }
                    }
                }
            }
        ) {
            Text(text = stringResource(id = R.string.register))
        }

    }
}
