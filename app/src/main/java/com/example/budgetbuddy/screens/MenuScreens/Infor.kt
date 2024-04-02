package com.example.budgetbuddy.screens.MenuScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.budgetbuddy.R
import com.example.budgetbuddy.shared.Titulo
import com.example.budgetbuddy.shared.compartirContenido

@Composable
fun Infor(
    onConfirm: () -> Unit
) {
    val context = LocalContext.current

    val shareMessage = stringResource(id = R.string.share_message)
    val asunto = stringResource(id = R.string.asunto)
    val contenidoMail = stringResource(id = R.string.contenidoEmail)

    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Titulo()
        Divider()
        Text(text = stringResource(id = R.string.app_description))
        Divider()
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
                Text(text =  stringResource(id = R.string.email))
            }
        }
        TextButton(onClick = { onConfirm() }
        ) {
            Text(text = stringResource(id = R.string.ok))
        }
    }
}