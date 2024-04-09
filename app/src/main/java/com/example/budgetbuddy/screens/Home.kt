package com.example.budgetbuddy.screens

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import com.example.budgetbuddy.Data.Enumeration.AppLanguage
import com.example.budgetbuddy.VM.AppViewModel
import com.example.budgetbuddy.Data.Room.Gasto
import com.example.budgetbuddy.Data.Enumeration.obtenerTipoEnIdioma
import com.example.budgetbuddy.R
import com.example.budgetbuddy.navigation.AppScreens
import com.example.budgetbuddy.shared.GastoAbierto
import com.example.budgetbuddy.shared.Header
import com.example.budgetbuddy.shared.NoData
import com.example.budgetbuddy.shared.ToastMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

/**************************************************
***             Pantalla Home                   ***
***************************************************/
/**
Este composable forma la pantalla de inicio de la aplicación.

Contiene la lista de gastos con sus respectivas Card (una por gasto).

Se le pasan los parámetros de:
    * @appViewModel:  ViewModel general de la aplicación con los flows de la información relativa a los gastos.
    * @idioma:        Necesario para la conversión de tipos de gasto.
    * @navController: De forma que se pueda acceder a las pantallas auxiliares de Add y Edit.
    * @modifier:      Para dar un estilo predeterminado a los composables (default).
    * @onEdit:        Función necesaria para actualizar los datos en caso de edición.
*/
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun Home(
    appViewModel: AppViewModel,
    idioma: AppLanguage,
    navController: NavController,
    modifier: Modifier = Modifier,
    onEdit: (Gasto)->Unit,
){

    /*******************************************************************
     **    Recoger el valor actual de cada flow del AppViewModel      **
     **                 (valor por defecto: initial)                  **
     ******************************************************************/
    val fecha by appViewModel.fecha.collectAsState(initial = LocalDate.now())
    val gastos by appViewModel.listadoGastosFecha(fecha).collectAsState(emptyList())

    /**    Parámetros para el control de los estados de los composables (Requisito 5)   **/
    var askPermission by remember {mutableStateOf(false)}

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Header(
            titulo = stringResource(id = R.string.list_explain, appViewModel.escribirFecha(fecha)),
            appViewModel = appViewModel
        )
        when {
            /** Listado de gastos (cada uno en su elemento Card) dentro de la LazyColumn (Requisito 1) **/
            gastos.isNotEmpty() -> {
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(6.dp),
                    contentPadding = PaddingValues(bottom = 30.dp)
                ) {
                    items(gastos) {
                        /** Elementos Card **/
                        var show by remember { mutableStateOf(false) }
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(3.dp),
                            shape = CardDefaults.elevatedShape,
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary)
                        ) {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier
                                        .padding(16.dp)
                                        .weight(3f)
                                ) {
                                    Text(text = it.nombre, fontWeight = FontWeight.Bold)
                                    Text(text = stringResource(id = R.string.cantidad, it.cantidad))
                                    Text(text = stringResource(id = R.string.tipo, obtenerTipoEnIdioma(it.tipo, idioma.code)))
                                }
                                /** Botones de edición y borrado **/
                                Button(
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Transparent
                                    ),onClick = { show = true }
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.eye),
                                        stringResource(id = R.string.infor),
                                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                                GastoAbierto(
                                    show = show,
                                    navController = navController,
                                    appViewModel = appViewModel,
                                    gasto = it,
                                    idioma = idioma,
                                    onEdit = { onEdit(it) }
                                ) {
                                    show = false
                                }
                            }
                        }
                    }
                }
            }

            else -> {
                NoData()
            }
        }
    }
}


