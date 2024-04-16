package com.example.budgetbuddy.Screens.MenuScreens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.TextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.budgetbuddy.Local.Room.User
import com.example.budgetbuddy.R
import com.example.budgetbuddy.VM.UserViewModel
import com.example.budgetbuddy.Shared.CloseButton
import com.example.budgetbuddy.Shared.Description
import com.example.budgetbuddy.Shared.ErrorText
import com.example.budgetbuddy.Shared.Subtitulo
import com.example.budgetbuddy.ui.theme.grisClaro
import com.example.budgetbuddy.ui.theme.verdeClaro
import com.example.budgetbuddy.ui.theme.verdeOscuro
import com.example.budgetbuddy.utils.correctName
import com.example.budgetbuddy.utils.correctPasswd
import com.example.budgetbuddy.utils.hash
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
@Composable
fun UserEdit(
    userViewModel: UserViewModel,
    currentUser: String,
    onConfirm: () -> Unit
){
    val coroutineScope = rememberCoroutineScope()

    var nombreOk by remember { mutableStateOf(true) }
    var passwdOk by remember { mutableStateOf(true) }

    var nombre by rememberSaveable { mutableStateOf("") }
    var passwd by rememberSaveable { mutableStateOf("") }
    var passwd2 by rememberSaveable { mutableStateOf("") }

    Column (
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Subtitulo(mensaje = stringResource(id = R.string.edit_profile), true)
        ///////////////////////////////////////// Campo de Nombre /////////////////////////////////////////
        TextField(
            value = nombre,
            onValueChange = {nombre = it},
            label = { Text(stringResource(id = R.string.name)) },
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 16.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )
        if (!nombreOk){
            ErrorText(text = stringResource(id = R.string.name_error))
        }
        ///////////////////////////////////////// Campo de Contraseña /////////////////////////////////////////
        Spacer(modifier = Modifier.padding(vertical = 10.dp))
        Text(text = stringResource(id = R.string.edit_passwd))
        Description(mensaje = stringResource(id = R.string.edit_passwd_desc))
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
        if (!passwdOk){
            ErrorText(text = stringResource(id = R.string.passwd_error))
        }
        androidx.compose.material.Button(
            modifier = Modifier.padding(10.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = verdeOscuro,
                contentColor = grisClaro,
                disabledBackgroundColor = grisClaro,
                disabledContentColor = verdeOscuro,
            ),
            onClick = {
                if (correctName(nombre)){
                    if ( (passwd=="" && passwd2=="")){
                        coroutineScope.launch(Dispatchers.IO) {
                            // Ejecuta el código que puede bloquear el hilo principal aquí
                            userViewModel.cambiarDatos(User(nombre, currentUser, userViewModel.currentUser.password))
                        }
                        onConfirm()
                    }else if(correctPasswd(passwd, passwd2)){
                        coroutineScope.launch(Dispatchers.IO) {
                            // Ejecuta el código que puede bloquear el hilo principal aquí
                            userViewModel.cambiarDatos(User(nombre, currentUser, passwd.hash()))
                        }
                        onConfirm()
                    }else{
                        passwdOk = false
                        nombreOk = true
                    }
                }else{
                    nombreOk = false
                    passwdOk = true
                }
            }
        ) {
            Text(
                text = stringResource(id = R.string.edit),
                Modifier.background(color = verdeOscuro),
                color = verdeClaro
            )
        }
        CloseButton { onConfirm() }
    }
}