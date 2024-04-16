package com.example.budgetbuddy2.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.budgetbuddy.Local.Data.AppLanguage
import com.example.budgetbuddy.VM.AppViewModel
import com.example.budgetbuddy.R
import com.example.budgetbuddy.Shared.Header
import com.example.budgetbuddy.Shared.NoData
import java.time.LocalDate

/**************************************************
 ***            Pantalla Facturas               ***
 ***************************************************/
/**
Este composable forma la pantalla de facturas de la aplicación.

Contiene la factura del día seleccionado en la APP.

Se le pasan los parámetros de:
 * @appViewModel:  ViewModel general de la aplicación con los flows de la información relativa a los elementos [Gasto] y [factura].
 * @idioma:        Necesario para la conversión de tipos de gasto.
 * @modifier:      Para dar un estilo predeterminado a los composables (default).
 */
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun Facturas(
    appViewModel: AppViewModel,
    idioma: AppLanguage,
    modifier: Modifier = Modifier
){

    /*******************************************************************
     **    Recoger el valor actual de cada flow del AppViewModel      **
     **                 (valor por defecto: initial)                  **
     ******************************************************************/
    val fecha by appViewModel.fecha.collectAsState(initial = LocalDate.now())
    val factura by appViewModel.facturaActual(fecha, idioma).collectAsState(initial = "")
    val gastos by appViewModel.listadoGastosFecha(fecha).collectAsState(emptyList())
    val totalGastos by appViewModel.totalGasto(fecha).collectAsState(0.0)

    Column (
        Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Header(
            titulo = stringResource(id = R.string.date, appViewModel.escribirFecha(fecha)),
            appViewModel = appViewModel
        )
        when {
            /**
             * Factura del día completo con todos los [Gasto] y el [totalGastos]
             */
            gastos.isNotEmpty() -> {
                Card(
                    shape = CardDefaults.outlinedShape,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.background
                    ),
                    elevation = CardDefaults.cardElevation(pressedElevation = 4.dp),
                    border = BorderStroke(width = 2.dp, color = Color.DarkGray),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .wrapContentWidth(align = Alignment.CenterHorizontally)
                ) {
                    // Cabecera
                    Text(
                        text = stringResource( id = R.string.factura_init, appViewModel.escribirFecha(fecha)),
                        modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp).fillMaxWidth(),
                        fontWeight = FontWeight.Bold
                    )
                    // Gastos
                    Text(
                        text = factura,
                        modifier.padding(start = 16.dp, end = 16.dp)
                    )
                    // Total
                    Text(
                        text = stringResource(id = R.string.factura_total, totalGastos),
                        modifier.padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }else->{
                NoData()
            }
        }
    }
}



