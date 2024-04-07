package com.example.budgetbuddy.screens.MenuScreens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.budgetbuddy.Data.Enumeration.AppLanguage
import com.example.budgetbuddy.Data.Enumeration.Tema
import com.example.budgetbuddy.Data.Enumeration.obtenerTema
import com.example.budgetbuddy.R
import com.example.budgetbuddy.shared.Titulo
import com.example.budgetbuddy.ui.theme.azulMedio
import com.example.budgetbuddy.ui.theme.morado1
import com.example.budgetbuddy.ui.theme.verdeOscuro


@Composable
fun Preferences(
    onLanguageChange:(AppLanguage) -> Unit,
    idioma: String,
    onThemeChange:(Int)->Unit,
    onConfirm: () -> Unit
) {
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Titulo()
        Divider()
        Text(text = stringResource(id = R.string.change_lang), modifier = Modifier.padding(10.dp))
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            for (i in AppLanguage.entries){
                Button(
                    onClick = {
                        onConfirm()
                        onLanguageChange(AppLanguage.getFromCode(i.code))},
                    Modifier.fillMaxWidth()
                ) {
                    Text(text = i.language)
                }
            }
        }
        Divider()
        Text(text = stringResource(id = R.string.change_theme), modifier = Modifier.padding(10.dp))
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
            Button(
                onClick = {
                    onThemeChange(0)
                    onConfirm()
                },
                Modifier
                    .weight(1f)
                    .padding(8.dp),
                colors = ButtonColors(
                    containerColor = verdeOscuro,
                    disabledContainerColor = verdeOscuro,
                    contentColor = verdeOscuro,
                    disabledContentColor = verdeOscuro
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
                    .weight(1f)
                    .padding(8.dp),
                colors = ButtonColors(
                    containerColor = azulMedio,
                    disabledContainerColor = azulMedio,
                    contentColor = azulMedio,
                    disabledContentColor = azulMedio
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
                    .weight(1f)
                    .padding(8.dp),
                colors = ButtonColors(
                    containerColor = morado1,
                    disabledContainerColor = morado1,
                    contentColor = morado1,
                    disabledContentColor = morado1
                )
            ) {
                Text(text = obtenerTema(Tema.Morado, idioma))
            }
        }
        TextButton(onClick = { onConfirm() }
        ) {
            Divider()
            Text(text = stringResource(id = R.string.ok))
        }
    }

}