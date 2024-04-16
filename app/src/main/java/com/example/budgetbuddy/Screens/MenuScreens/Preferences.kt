package com.example.budgetbuddy.Screens.MenuScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.budgetbuddy.Local.Data.AppLanguage
import com.example.budgetbuddy.R
import com.example.budgetbuddy.Shared.CloseButton
import com.example.budgetbuddy.Shared.Description
import com.example.budgetbuddy.Shared.Subtitulo
import com.example.budgetbuddy.Shared.Titulo
import com.example.budgetbuddy.ui.theme.azulMedio
import com.example.budgetbuddy.ui.theme.morado1
import com.example.budgetbuddy.ui.theme.verdeOscuro


@Composable
fun Preferences(
    onLanguageChange:(AppLanguage) -> Unit,
    onThemeChange: (Int) -> Unit,
    onSaveChange: () -> Unit,
    onSaveLocation: () -> Unit,
    saveChange: Boolean,
    saveLocation: Boolean,
    onConfirm: () -> Unit
) {
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){

        var checked by rememberSaveable { mutableStateOf(saveChange) }
        var checked2 by rememberSaveable { mutableStateOf(saveLocation) }

        Titulo()
        Subtitulo(mensaje = stringResource(id = R.string.change_lang))
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
        Subtitulo(mensaje = stringResource(id = R.string.change_theme))
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
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
                Text(text = "")
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
                Text(text = "")
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
                Text(text = "")
            }
        }
        Subtitulo(mensaje = stringResource(id = R.string.ajustes))
        Column (Modifier.fillMaxWidth()){
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ){
                Text(
                    text = stringResource(id = R.string.guardar_calendario),
                    modifier = Modifier.weight(4f)
                )
                Checkbox(
                    checked = checked,
                    onCheckedChange = {
                        checked = it
                        onSaveChange()},
                    modifier = Modifier.weight(1f)
                )
            }
            Description(mensaje = stringResource(id = R.string.guardar_calendario_desc))
        }
        Column (Modifier.fillMaxWidth()){
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ){
                Text(
                    text = stringResource(id = R.string.guardar_loc),
                    modifier = Modifier.weight(4f)
                )
                Checkbox(
                    checked = checked2,
                    onCheckedChange = {
                        checked2 = it
                        onSaveLocation()},
                    modifier = Modifier.weight(1f)
                )
            }
            Description(mensaje = stringResource(id = R.string.guardar_loc_desc))
        }

        CloseButton { onConfirm() }

    }

}