package com.example.budgetbuddy.shared

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.budgetbuddy.Data.Enumeration.AppLanguage
import com.example.budgetbuddy.Data.Enumeration.Tema
import com.example.budgetbuddy.Data.Enumeration.obtenerTema
import com.example.budgetbuddy.R
import com.example.budgetbuddy.VM.AppViewModel
import com.example.budgetbuddy.navigation.AppScreens
import com.example.budgetbuddy.ui.theme.azulMedio
import com.example.budgetbuddy.ui.theme.morado1
import com.example.budgetbuddy.ui.theme.verdeOscuro
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId


/***********************************************
 **    Composables comunes de la aplicación   **
 ***********************************************/

// Estos son composables que se utilizan en diferentes pantallas
// y son independientes del resto de contenido de estas.

@Composable
fun Titulo(){
    Text(
        text = stringResource(id = R.string.app_name),
        modifier = Modifier.padding(vertical = 26.dp),
        style = TextStyle(
            color = MaterialTheme.colorScheme.primary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    )
}
/**
 * Cabecera de las tres pantallas principales para elegir la fecha
 * de la que se quiere visualizar la información
 */
@Composable
fun Header(
    titulo: String,
    appViewModel: AppViewModel
){
    var showCalendar by remember { mutableStateOf(false) }
    val onCalendarConfirm: (LocalDate) -> Unit = {
        showCalendar = false
        appViewModel.cambiarFecha(it)
    }
    Text(
        text = titulo,
        Modifier.padding(top=16.dp, bottom = 10.dp)
    )
    Button(
        onClick = { showCalendar = true }
    ) {
        Text(text = stringResource(id = R.string.date_pick))
    }
    Calendario(
        show = showCalendar,
        onCalendarConfirm
    )
    Divider()
}

@Composable
fun Perfil(
    appViewModel: AppViewModel,
    modifier: Modifier
){
    Column (
        modifier = modifier.background(color = MaterialTheme.colorScheme.tertiary),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Column (
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.user),
                contentDescription = stringResource(id = R.string.profile_picture),
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Dos textos de ejemplo
            Text(
                text = appViewModel.currentUserName,
                modifier = Modifier.padding(5.dp),
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onTertiary
            )

            Text(
                text = appViewModel.currentUser,
                modifier = Modifier.padding(5.dp),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onTertiary
            )
        }
        }
}

/**
 * Calendario para seleccionar la fecha en las diferentes pantallas y formularios
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Calendario(show: Boolean, onConfirm: (LocalDate) -> Unit){
    if (show){
        val state = rememberDatePickerState()
        var date by remember { mutableStateOf(LocalDate.now()) }
        DatePickerDialog(
            onDismissRequest = { onConfirm(date) },
            confirmButton = {
                Button(onClick = { onConfirm(date) }) {
                    Text(text = stringResource(id = R.string.ok))
                }
            }
        ) {
            DatePicker(state = state)
            var time = state.selectedDateMillis?:System.currentTimeMillis()
            date = Instant.ofEpochMilli(time).atZone(ZoneId.of("UTC")).toLocalDate()
        }
    }
}

/**
 * Mensaje ante la falta de datos
 */
@Composable
fun NoData(){
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Puedes personalizar el ícono de falta de datos según tus necesidades
        Icon(
            painter = painterResource(id = R.drawable.close),
            contentDescription = null, // Descripción para accesibilidad
            modifier = Modifier
                .size(120.dp)
                .padding(12.dp),
            tint = Color.Gray // Color del ícono
        )
        Text(
            text = stringResource(id = R.string.no_data),
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
        )

    }
}

@Composable
fun ErrorText(text: String) {
    Text(
        buildAnnotatedString {
            withStyle(style = SpanStyle(color = Color.Red, fontStyle = FontStyle.Italic, fontSize = 14.sp)) {
                append(text)
            }
        },
        modifier = Modifier.padding(2.dp)
    )
}

