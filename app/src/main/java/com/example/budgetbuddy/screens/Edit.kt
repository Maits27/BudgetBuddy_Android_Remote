package com.example.budgetbuddy.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import com.example.budgetbuddy.VM.AppViewModel
import com.example.budgetbuddy.Data.Room.Gasto
import com.example.budgetbuddy.Data.Enumeration.TipoGasto
import com.example.budgetbuddy.Data.Enumeration.obtenerTipoEnIdioma
import com.example.budgetbuddy.R
import com.example.budgetbuddy.VM.PreferencesViewModel
import com.example.budgetbuddy.shared.Calendario
import com.example.budgetbuddy.shared.ErrorAlert
import com.example.budgetbuddy.shared.MapScreen
import com.example.budgetbuddy.shared.Subtitulo
import com.example.budgetbuddy.shared.ToastMessage
import com.example.budgetbuddy.ui.theme.grisClaro
import com.example.budgetbuddy.utils.LocationPermission
import com.example.budgetbuddy.utils.agregarGastoAlCalendario
import com.example.budgetbuddy.utils.toLong
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeParseException

/***************************************************
 ***          Pantalla auxiliar Edit             ***
 ***************************************************/
/**
Este composable forma la pantalla del formulario para editar elementos.

Se le pasan los parámetros de:
 * @gasto:          elemento tipo [Gasto] que se quiere editar (con sus datos hasta el momento).
 * @ppViewModel:  ViewModel general de la aplicación con los flows de la información relativa a la [Fecha].
 * @navController: De forma que se pueda volver a la pantalla [Home].
 * @idioma:        Necesario para la conversión de tipos de gasto.
 * @modifier:      Para dar un estilo predeterminado a los composables (default).
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
@Composable
fun Edit(
    gasto: Gasto,
    appViewModel: AppViewModel,
    preferencesViewModel: PreferencesViewModel,
    navController: NavController,
    fusedLocationClient: FusedLocationProviderClient,
    modifier: Modifier = Modifier.verticalScroll(rememberScrollState())
){
    Log.d("GASTO","Gasto to edit: $gasto")
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    /*******************************************************************
     **    Recoger el valor actual de cada flow del AppViewModel      **
     **                 (valor por defecto: initial)                  **
     ******************************************************************/
    val fecha by appViewModel.fecha.collectAsState(initial = LocalDate.now())
    val idioma by preferencesViewModel.idioma(appViewModel.currentUser).collectAsState(initial = preferencesViewModel.currentSetLang)
    val saveLoc by preferencesViewModel.saveLocation(appViewModel.currentUser).collectAsState(initial = true)

    /*******************************************************************
     **                     Valores del formulario                    **
     * (rememberSaveable para no perder datos en caso de interrupción) *
     ******************************************************************/
    var nombre by rememberSaveable { mutableStateOf(gasto.nombre) }
    var euros by rememberSaveable { mutableStateOf(gasto.cantidad.toString()) }
    var selectedOption by rememberSaveable { mutableStateOf(gasto.tipo) }
    var fechaTemporal by rememberSaveable {mutableStateOf(fecha)}
    var lastKnownLocation: Location? = null

    /**    Parámetros para el control de los estados de los composables (Requisito 5)   **/
    var error_message by remember { mutableStateOf("") }
    var isTextFieldFocused by remember { mutableStateOf(-1) }
    var showError by rememberSaveable { mutableStateOf(false) }
    var enabledDate by remember { mutableStateOf(true) }
    var showToast by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var changeDate by remember { mutableStateOf(false) }

    /**    Funciones parámetro para gestionar las acciones del estado   **/
    val onCalendarConfirm: (LocalDate) -> Unit = {
        isTextFieldFocused = -1
        fechaTemporal=it
        enabledDate = false
        changeDate = true
    }
    if (ActivityCompat.checkSelfPermission(
            LocalContext.current,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            LocalContext.current,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        LocationPermission()
    }
    fusedLocationClient.lastLocation
        .addOnSuccessListener { location: Location? ->
            // Got last known location. In some rare situations this can be null.
            Log.d("LOCATION", "POST EDIT: ${location.toString()}")
            lastKnownLocation = location
        }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Subtitulo(mensaje = stringResource(id = R.string.edit), true)

        ///////////////////////////////////////// Campo de Nombre /////////////////////////////////////////
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text(stringResource(id = R.string.name_element)) },
            keyboardActions = KeyboardActions(
                onDone = {
                    isTextFieldFocused = -1
                    keyboardController?.hide()
                }
            ),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .onFocusChanged {
                    if (it.isFocused) {
                        isTextFieldFocused = 0
                    }
                }
        )

        ///////////////////////////////////////// Campo de Tipo /////////////////////////////////////////
        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = { expanded = true }),
                shape = MaterialTheme.shapes.small,
                border = BorderStroke(1.dp, Color.DarkGray),
                color = grisClaro
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = obtenerTipoEnIdioma(selectedOption, idioma.code),
                        modifier = Modifier.padding(16.dp),
                        color = Color.DarkGray
                    )
                    Row(
                        horizontalArrangement = Arrangement.End
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 12.dp),
                            tint = Color.DarkGray
                        )
                    }

                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(color = MaterialTheme.colors.background),
            ) {
                TipoGasto.entries.forEach { option ->
                    DropdownMenuItem(
                        onClick = {
                            selectedOption = option
                            expanded = false
                        },
                        modifier = Modifier.background(color = MaterialTheme.colors.background)
                    ) {
                        Text(
                            text = obtenerTipoEnIdioma(option, idioma.code),
                            Modifier.background(color = MaterialTheme.colors.background)
                        )
                    }
                }
            }
        }

        ///////////////////////////////////////// Campo de Cantidad /////////////////////////////////////////
        OutlinedTextField(
            value = euros,
            onValueChange = { euros = it },
            label = { Text(stringResource(id = R.string.price_element)) },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    isTextFieldFocused = -1
                    keyboardController?.hide()
                }
            ),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .onFocusChanged {
                    if (it.isFocused) {
                        isTextFieldFocused = 1
                    }
                }
        )

        ///////////////////////////////////////// Campo de Fecha /////////////////////////////////////////
        if(!changeDate && fechaTemporal!=fecha){fechaTemporal = fecha}
        OutlinedTextField(
            value = if(changeDate){fechaTemporal.toString()}else{fecha.toString()},
            onValueChange = {
                fechaTemporal = try {
                    LocalDate.parse(it)
                } catch (e: DateTimeParseException) {
                    // Asigna un valor predeterminado en caso de introducir un valor que no sea tipo LocalDate
                    fecha
                }
            },
            label = { Text(stringResource(id = R.string.date_pick)) },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        isTextFieldFocused = 2 // Cuando el campo de texto está enfocado
                    } else {
                        isTextFieldFocused = -1 // Cuando el campo de texto pierde el enfoque
                    }
                },
            enabled = enabledDate,
            keyboardOptions = KeyboardOptions.Default.copy(showKeyboardOnFocus = false)
        )
        if (!enabledDate) { enabledDate = true }

        Calendario(show = (isTextFieldFocused == 2), onCalendarConfirm)

        val location = Location("")
        location.latitude = gasto.latitud
        location.longitude = gasto.longitud
        if (saveLoc) MapScreen(lastKnownLocation = location)

        /** Botón para guardar los cambios en Room **/
        Button(
            onClick = {
                // Lanzamiento de corrutina:
                // En caso de bloqueo o congelado de la base de datos, para que no afecte al uso normal y fluido de la aplicación.
                // (Necedario en los métodos de tipo insert, delete y update)
                coroutineScope.launch(Dispatchers.IO) {
                    if (nombre != "" && euros != "") {
                        if (euros.toDoubleOrNull() != null) {
                            appViewModel.cambiarFecha(fecha)
                            appViewModel.editarGasto(
                                gasto,
                                nombre,
                                euros.toDouble(),
                                fechaTemporal,
                                selectedOption,
                                latitud = if (saveLoc){lastKnownLocation?.latitude?:0.0}else{0.0},
                                longitud = if (saveLoc){lastKnownLocation?.longitude?:0.0}else{0.0}
                            )
                            agregarGastoAlCalendario(context, "BUDGET BUDDY", "$nombre (${selectedOption.tipo}): $euros€", fecha.toLong())
                        } else {
                            showError = true
                            error_message = context.getString(R.string.error_double)
                        }
                    } else {
                        showError = true
                        error_message = context.getString(R.string.error_insert)
                    }
                    if (!showError) {
                        showToast=true
                        withContext(Dispatchers.Main) {
                            navController.navigateUp()
                        }
                    }
                }
            },
            Modifier
                .padding(8.dp, 16.dp)
        ) {
            Text(text = stringResource(id = R.string.edit))
        }
        if(showToast){
            ToastMessage(LocalContext.current, message = stringResource(id = R.string.edit_complete, nombre))
        }

        ErrorAlert(show = showError, mensaje = error_message) { showError = false }
    }
}
