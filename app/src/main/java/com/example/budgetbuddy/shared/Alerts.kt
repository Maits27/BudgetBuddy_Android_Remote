package com.example.budgetbuddy.shared

import android.content.ContentResolver
import android.content.Context
import android.location.Location
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.budgetbuddy.Data.Enumeration.AppLanguage
import com.example.budgetbuddy.Data.Enumeration.Tema
import com.example.budgetbuddy.Data.Enumeration.obtenerTema
import com.example.budgetbuddy.Data.Enumeration.obtenerTipoEnIdioma
import com.example.budgetbuddy.Data.Room.Gasto
import com.example.budgetbuddy.R
import com.example.budgetbuddy.VM.AppViewModel
import com.example.budgetbuddy.navigation.AppScreens
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/************************************
 **    Diálogos de la aplicación   **
 ************************************/

// Aquí se implementan todos los diálogos (o AlertDialog) de la aplicación. (Requisito 3)


/**
 * Alerta de error por defecto:
 * Este diálogo se utiliza para informar de cualquier error (generado por el usuario
 * o por ciertos problemas de la misma APP, generalmente con el timing) ocurrido en
 * la aplicación. Solo será necesario cambiar el mensaje del contenido.
 */
@Composable
fun ErrorAlert(show: Boolean, mensaje: String, onConfirm: () -> Unit) {
    if(show){
        AlertDialog(
            containerColor = MaterialTheme.colorScheme.background,
            onDismissRequest = {},
            confirmButton = { TextButton(onClick = { onConfirm() }) {
                Text(text = stringResource(id = R.string.ok))
            }
            },
            title = { Text(text = stringResource(id = R.string.error), color = MaterialTheme.colorScheme.onError) },
            text = {
                Text(text = mensaje, color = Color.Black)
            }
        )
    }
}

/**
 * Mensaje Toast customizable mediante [message]
 */
@Composable
fun ToastMessage(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}


/**
 * Despliegue de Card de Gasto
 */

@Composable
fun GastoAbierto(
    show: Boolean,
    navController: NavController,
    appViewModel: AppViewModel,
    gasto: Gasto,
    idioma: AppLanguage,
    onEdit: (Gasto) -> Unit,
    onConfirm: () -> Unit
){
    val coroutineScope = rememberCoroutineScope()
    var toast by remember { mutableStateOf("") }
    if(show){
        AlertDialog(
            containerColor = MaterialTheme.colorScheme.tertiary,
            onDismissRequest = {},
            confirmButton = { TextButton(onClick = { onConfirm() }) {
                Text(text = stringResource(id = R.string.ok))
            }
            },
            title = {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = gasto.nombre,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(4f)
                    )
                    /** Botones de edición y borrado **/
                    IconButton(
                        onClick = {
                            // Lanzamiento de corrutina:
                            // En caso de bloqueo o congelado de la base de datos, para que no afecte al uso normal y fluido de la aplicación.
                            // (Necedario en los métodos de tipo insert, delete y update)
                            coroutineScope.launch(Dispatchers.IO) { onEdit(gasto) }

                            navController.navigate(AppScreens.Edit.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                            onConfirm() },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.Transparent
                        ),
                        modifier = Modifier.size(20.dp).weight(1f), // Tamaño del icono dentro del botón redondo
                    ){
                        Icon(
                            Icons.Filled.Edit,
                            contentDescription = stringResource(id = R.string.edit),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }

                    IconButton(
                        onClick = {
                            // Lanzamiento de corrutina:
                            // En caso de bloqueo o congelado de la base de datos, para que no afecte al uso normal y fluido de la aplicación.
                            // (Necedario en los métodos de tipo insert, delete y update)
                            toast = gasto.nombre
                            coroutineScope.launch(Dispatchers.IO) {appViewModel.borrarGasto(gasto)}
                            onConfirm() },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.Transparent
                        ),
                        modifier = Modifier.size(20.dp).weight(1f), // Tamaño del icono dentro del botón redondo
                    ){
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = stringResource(id = R.string.delete),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }
                }
            },
            text = {
                if( toast != "" ){
                    ToastMessage(LocalContext.current, message = stringResource(id = R.string.delete_complete, toast))
                    toast = ""
                }
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    CardElement(text = stringResource(id = R.string.cantidad, gasto.cantidad))
                    CardElement(text = stringResource(id = R.string.tipo, obtenerTipoEnIdioma(gasto.tipo, idioma.code)))
                    val location = Location("")
                    location.latitude = gasto.latitud
                    location.longitude = gasto.longitud
                    MapScreen(lastKnownLocation = location)
                }
            }
        )
    }
}