package com.example.budgetbuddy.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.example.budgetbuddy.VM.PreferencesViewModel

/**
 * Tema [Verde] (default) de la aplicación
 */
private val DarkColorScheme = lightColorScheme(
    primary = verdeOscuro,
    secondary = verdeOscuro,
    tertiary = verde5,
    background = grisClaro,
    onPrimary = grisClaro,
    onSecondary = grisClaro,
    onTertiary = verdeOscuro,
    primaryContainer = verdeOscuro,
    onError = rojoError,
    onBackground = Color.DarkGray,
    onPrimaryContainer = grisClaro,
    onSecondaryContainer = verdeOscuro,
    onErrorContainer =  errorCont,
    surface = Color.White,
    onSurface = verdeOscuro,
    onSurfaceVariant = verdeOscuro,
    inverseOnSurface = verdeOscuro,
)
/**
 * Tema [Azul] de la aplicación
 */
private val LightColorScheme = lightColorScheme(
    primary = azulOscuro,
    secondary = azulOscuro,
    tertiary = azulClaro,
    background = grisClaro,
    onPrimary = grisClaro,
    onSecondary = grisClaro,
    onTertiary = azulMedio,
    primaryContainer = azulOscuro,
    onError = rojoError,
    onBackground = Color.DarkGray,
    onPrimaryContainer = grisClaro,
    onSecondaryContainer = azulOscuro,
)

/**
 * Tema [Morado] de la aplicación
 */
private val CustomColorScheme = lightColorScheme(
    primary = moradoOscuro,
    secondary = moradoOscuro,
    tertiary = rosaClaro,
    background = grisClaro,
    onPrimary = grisClaro,
    onSecondary = grisClaro,
    onTertiary = moradoOscuro,
    primaryContainer = moradoOscuro,
    onError = rojoError,
    onBackground = Color.DarkGray,
    onPrimaryContainer = grisClaro,
    onSecondaryContainer = moradoOscuro,
    onErrorContainer =  errorCont,
    surface = Color.White,
    onSurface = moradoOscuro,
    onSurfaceVariant = moradoOscuro,
    inverseOnSurface = moradoOscuro

)

/**
 * Selector del tema de la aplicación
 */
@Composable
fun BudgetBuddyTheme(
    user: String,
    preferencesViewModel: PreferencesViewModel,
    content: @Composable () -> Unit
) {
    val theme by preferencesViewModel.theme(user).collectAsState(initial = true)
    if (theme==0){
        MaterialTheme(
            colorScheme = DarkColorScheme,
            typography = Typography,
            content = content
        )
    }else if (theme==1){
        MaterialTheme(
            colorScheme = LightColorScheme,
            typography = Typography,
            content = content
        )
    }else{
        MaterialTheme(
            colorScheme = CustomColorScheme,
            typography = Typography,
            content = content
        )
    }
}


/**
 * Selector del gráfico [Pastel] de la pantalla [Dashboards].
 */
fun dashboardTheme(theme: Int): List<Color>{
    if (theme == 0) {
        return listOf(
            verde1,
            verde2,
            verde3,
            verde4,
            verde5,
            verde6,
        )
    } else if (theme == 1) {
        return listOf(
            azul1,
            azul2,
            azul3,
            azul4,
            azul5,
            azul6
        )
    } else {
        return listOf(
            morado1,
            morado2,
            morado3,
            morado4,
            morado5,
            morado6
        )
    }


}