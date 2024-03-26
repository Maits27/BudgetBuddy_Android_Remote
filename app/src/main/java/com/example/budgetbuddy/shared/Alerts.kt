package com.example.budgetbuddy.shared

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.budgetbuddy.Data.Enumeration.AppLanguage
import com.example.budgetbuddy.Data.Enumeration.Tema
import com.example.budgetbuddy.Data.Enumeration.obtenerTema
import com.example.budgetbuddy.R


/************************************
 **    Diálogos de la aplicación   **
 ************************************/

// Aquí se implementan todos los diálogos (o AlertDialog) de la aplicación. (Requisito 3)

/**
 * Diálogos de la barra superior del Scaffold:
 *  Aquí se definen las tres opciones de la barra superior:
 *      - Información
 *      - Idiomas
 *      - Temas
 */
@Composable
fun Informacion(show: Boolean, onConfirm: () -> Unit) {
    val context = LocalContext.current

    val shareMessage = stringResource(id = R.string.share_message)
    val asunto = stringResource(id = R.string.asunto)
    val contenidoMail = stringResource(id = R.string.contenidoEmail)

    if(show){
        AlertDialog(
            onDismissRequest = {},
            containerColor = MaterialTheme.colorScheme.background,
            confirmButton = {
                Row {
                    TextButton(onClick = {
                        compartirContenido(context, shareMessage)
                        onConfirm()
                    }
                    ) {
                        Text(text =  stringResource(id = R.string.share))
                    }
                    TextButton(onClick = {
                        compartirContenido(context, contenidoMail, asunto = asunto)
                        onConfirm()
                    }
                    ) {
                        Text(text =  "Email")
                    }
                }
            }, dismissButton = {
                TextButton(onClick = { onConfirm() }
                ) {
                    Text(text = stringResource(id = R.string.ok))
                }
            },
            title = { Text(text = stringResource(id = R.string.app_name)) },
            text = {
                Text(text = stringResource(id = R.string.app_description))
            }
        )
    }
}

@Composable
fun Idiomas(
    show: Boolean,
    onLanguageChange:(AppLanguage)->Unit,
    onConfirm: () -> Unit
) {
    if(show){
        AlertDialog(
            onDismissRequest = {},
            containerColor = MaterialTheme.colorScheme.background,
            confirmButton = { TextButton(onClick = { onConfirm() }){
                Text(text = stringResource(id = R.string.ok))
            }
            },
            title = { Text(text = stringResource(id = R.string.change_lang)) },
            text = {
                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ){
                    for (idioma in AppLanguage.entries){
                        Button(
                            onClick = {
                                onConfirm()
                                onLanguageChange(AppLanguage.getFromCode(idioma.code))},
                            Modifier.fillMaxWidth()
                        ) {
                            Text(text = idioma.language)
                        }
                    }
                }
            }
        )
    }
}
@Composable
fun Temas(
    show: Boolean,
    idioma:String,
    onThemeChange:(Int)->Unit,
    onConfirm: () -> Unit
) {
    if(show){
        AlertDialog(
            onDismissRequest = {},
            containerColor = MaterialTheme.colorScheme.background,
            confirmButton = { TextButton(onClick = { onConfirm() }){
                Text(text = stringResource(id = R.string.ok))
            }
            },
            title = { Text(text = stringResource(id = R.string.change_theme)) },
            text = {
                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ){
                    Button(
                        onClick = {
                            onThemeChange(0)
                            onConfirm()
                            },
                        Modifier
                            .fillMaxWidth(),
                        colors = ButtonColors(
                            containerColor = Color(0xffCFFFDB),
                            disabledContainerColor = Color(0xffCFFFDB),
                            contentColor = Color(0xff082e20),
                            disabledContentColor = Color(0xff082e20)
                        )
                    ) {
                        Text(text = obtenerTema(Tema.Verde, idioma))
                    }
                    Button(
                        onClick = {
                            onThemeChange(1)
                            onConfirm()
                            },
                        Modifier
                            .fillMaxWidth(),
                        colors = ButtonColors(
                            containerColor = Color(0xffCFE4FF),
                            disabledContainerColor = Color(0xffCFE4FF),
                            contentColor = Color(0xff0E2D68),
                            disabledContentColor = Color(0xff0E2D68)
                        )
                    ) {
                        Text(text = obtenerTema(Tema.Azul, idioma))
                    }
                    Button(
                        onClick = {
                            onThemeChange(2)
                            onConfirm()
                        },
                        Modifier
                            .fillMaxWidth(),
                        colors = ButtonColors(
                            containerColor = Color(0xffEBCFFF),
                            disabledContainerColor = Color(0xffEBCFFF),
                            contentColor = Color(0xff4A126E),
                            disabledContentColor = Color(0xff4A126E)
                        )
                    ) {
                        Text(text = obtenerTema(Tema.Morado, idioma))
                    }
                }
            }
        )
    }
}

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