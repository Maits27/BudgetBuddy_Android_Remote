package com.example.budgetbuddy.screens.MenuScreens

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.budgetbuddy.Data.Enumeration.TipoGasto
import com.example.budgetbuddy.Data.Enumeration.obtenerTipoEnIdioma
import com.example.budgetbuddy.Data.Room.Gasto
import com.example.budgetbuddy.Data.Room.User
import com.example.budgetbuddy.R
import com.example.budgetbuddy.UserVerification.correctName
import com.example.budgetbuddy.UserVerification.correctPasswd
import com.example.budgetbuddy.VM.AppViewModel
import com.example.budgetbuddy.VM.UserViewModel
import com.example.budgetbuddy.navigation.AppScreens
import com.example.budgetbuddy.shared.Calendario
import com.example.budgetbuddy.shared.CloseButton
import com.example.budgetbuddy.shared.Description
import com.example.budgetbuddy.shared.ErrorAlert
import com.example.budgetbuddy.shared.ErrorText
import com.example.budgetbuddy.shared.Subtitulo
import com.example.budgetbuddy.shared.ToastMessage
import com.example.budgetbuddy.ui.theme.grisClaro
import com.example.budgetbuddy.ui.theme.verdeClaro
import com.example.budgetbuddy.ui.theme.verdeOscuro
import com.example.budgetbuddy.utils.hash
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeParseException

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
@Composable
fun UserEdit(
    userViewModel: UserViewModel,
    currentUser: String,
    onConfirm: () -> Unit
){
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val editComplete = stringResource(id = R.string.user_edit_complete)

    var nombreOk by remember { mutableStateOf(true) }
    var passwdOk by remember { mutableStateOf(true) }

    var nombre by rememberSaveable { mutableStateOf("") }
    var passwd by rememberSaveable { mutableStateOf("") }
    var passwd2 by rememberSaveable { mutableStateOf("") }

    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Subtitulo(mensaje = stringResource(id = R.string.edit_profile), true)
        ///////////////////////////////////////// Campo de Nombre /////////////////////////////////////////
        TextField(
            value = nombre,
            onValueChange = {nombre = it},
            label = { Text(stringResource(id = R.string.name)) },
            modifier = Modifier.padding(10.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )
        if (!nombreOk){
            ErrorText(text = stringResource(id = R.string.name_error))
        }
        ///////////////////////////////////////// Campo de Contraseña /////////////////////////////////////////
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