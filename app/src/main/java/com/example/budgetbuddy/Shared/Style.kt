package com.example.budgetbuddy.Shared

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.budgetbuddy.R
import com.example.budgetbuddy.ui.theme.grisClaro
import com.example.budgetbuddy.ui.theme.naranjita
import com.example.budgetbuddy.ui.theme.rojito
import com.example.budgetbuddy.ui.theme.verde5
import com.example.budgetbuddy.ui.theme.verdeOscuro

/************************************************************************
 **    Composables para definir el estilo de los elementos de la app   **
 ************************************************************************/


/**
 * Título de la aplicación con el nombre de la APP.
 */
@Composable
fun Titulo(login: Boolean = false){
    var colorTexto = MaterialTheme.colorScheme.primary
    var size = 24.sp
    var pad = 26.dp
    if (login) {
        colorTexto = verdeOscuro
        size = 36.sp
        pad = 10.dp
    }
    Text(
        text = stringResource(id = R.string.app_name),
        modifier = Modifier.padding(vertical = pad),
        style = TextStyle(
            color = colorTexto,
            fontSize = size,
            fontWeight = FontWeight.Bold
        )
    )
}

/**
 * Subtítulo o título de sección, después del nombre de la aplicación.
 */
@Composable
fun Subtitulo(mensaje: String, login: Boolean = false){
    if (!login) HorizontalDivider(color = Color.DarkGray, thickness = 1.dp)
    Text(
        text = mensaje,
        modifier = Modifier.padding(vertical = 18.dp),
        style = TextStyle(
            color = Color.DarkGray,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    )
    if (login) HorizontalDivider(color = Color.DarkGray, thickness = 2.dp)
}

/**
 * Descripción de elemento en letra pequeña para diferentes aclaraciones.
 */
@Composable
fun Description(mensaje: String){
    Text(
        text = mensaje,
        modifier = Modifier.padding(vertical = 2.dp),
        style = TextStyle(
            color = Color.DarkGray,
            fontSize = 10.sp,
            fontStyle = FontStyle.Italic
        )
    )
}

/**
 * Elemento Card para la lista de [Gasto] en [Home]
 */

@Composable
fun CardElement(text: String, kant: Double = 0.0, modifier:Modifier = Modifier){
    var colorGasto = grisClaro
    if (kant > 100) colorGasto = rojito
    else if ( kant > 50) colorGasto = naranjita
    else if ( kant > 0) colorGasto = verde5
    Text(
        text = text,
        modifier = modifier
            .background(colorGasto, shape = RoundedCornerShape(8.dp))
            .padding(8.dp),
        style = TextStyle(
            color = MaterialTheme.colorScheme.primary,
            fontSize = 18.sp
        )
    )

}

/**
 * Mensaje de error customizable
 */
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

/**
 * Botón de cerrado con formato y texto fijos.
 */
@Composable
fun CloseButton(onConfirm: () -> Unit){
    Button(
        modifier = Modifier.padding(16.dp),
        onClick = { onConfirm()}
    ) {
        Text(text = stringResource(id = R.string.ok))
    }
}


/**
 * Icono en movimiento para el [Perfil] o la carga de datos al servidor
 */
@Composable
fun LoadingImagePlaceholder(size: Dp = 140.dp, id: Int = R.drawable.start_icon) {
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                // One iteration is 1000 milliseconds.
                durationMillis = 1000
                // 0.7f at the middle of an iteration.
                0.7f at 500
            },
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    Image(
        modifier = Modifier
            .size(size)
            .clip(
                if (id == R.drawable.start_icon) {
                    CircleShape
                } else {
                    RectangleShape
                }
            )
            .alpha(alpha),
        painter = painterResource(id = id),
        contentDescription = null,
        contentScale = ContentScale.Crop
    )
}