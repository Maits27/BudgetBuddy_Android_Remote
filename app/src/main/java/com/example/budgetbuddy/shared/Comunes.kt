package com.example.budgetbuddy.shared

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.example.budgetbuddy.R
import com.example.budgetbuddy.VM.AppViewModel
import com.example.budgetbuddy.VM.UserViewModel
import com.example.budgetbuddy.screens.LocationPermission
import com.example.budgetbuddy.ui.theme.verdeOscuro
import com.example.budgetbuddy.utils.locationToLatLng
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId



/***********************************************
 **    Composables comunes de la aplicación   **
 ***********************************************/

// Estos son composables que se utilizan en diferentes pantallas
// y son independientes del resto de contenido de estas.
@Composable
fun MapScreen(
    lastKnownLocation: Location?,
    title: String = "",
    snippet: String = ""
) {
    when{
        (lastKnownLocation!=null
                && lastKnownLocation.latitude!=0.0
                && lastKnownLocation.longitude!=0.0) ->{
                    Box (
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(16.dp)
                            .border(2.dp, MaterialTheme.colorScheme.primary)
                    ){

                        val location = locationToLatLng(lastKnownLocation)
                        val cameraPositionState = rememberCameraPositionState {
                            position = CameraPosition.fromLatLngZoom(location, 10f)
                        }
                        GoogleMap(
                            modifier = Modifier.size(width = 300.dp, height = 400.dp),
                            cameraPositionState = cameraPositionState
                        ) {
                            Marker(
                                state = MarkerState(position = location),
                                title = title,
                                snippet = snippet
                            )
                        }
                }
        }else-> {
            NoMap()
        }
    }
}

@Composable
fun NoMap(){
    Column (
        modifier = Modifier
            .wrapContentSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Icon(
            painter = painterResource(id = R.drawable.nomap_bw),
            tint = MaterialTheme.colorScheme.onTertiary,
            contentDescription = "",
            modifier = Modifier.padding(10.dp).size(200.dp)
        )
        if(ActivityCompat.checkSelfPermission(
                LocalContext.current,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                LocalContext.current,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED){

            Text(text = "Give Location Permissions.")

        }else{
            Text(text = "Este gasto no tiene localización añadida.")
        }
    }
}
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
@Composable
fun CardElement(text: String){
    Row (
        modifier = Modifier.padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        Icon(
            painter = painterResource(id = R.drawable.circle),
            contentDescription = "",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(horizontal = 5.dp)
                .size(5.dp)
        )
        Text(
            text = text,
            style = TextStyle(
                color = MaterialTheme.colorScheme.primary,
                fontSize = 18.sp
            )
        )
    }
}
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
 * Cabecera de las tres pantallas principales para elegir la fecha
 * de la que se quiere visualizar la información
 */
@Composable
fun Header(
    titulo: String,
    appViewModel: AppViewModel
){
    var showCalendar by remember { mutableStateOf(false) }
    val onCalendarConfirm: (LocalDate) -> Unit = {
        showCalendar = false
        appViewModel.cambiarFecha(it)
    }
    Text(
        text = titulo,
        Modifier.padding(top=16.dp, bottom = 10.dp)
    )
    Button(
        onClick = { showCalendar = true }
    ) {
        Text(text = stringResource(id = R.string.date_pick))
    }
    Calendario(
        show = showCalendar,
        onCalendarConfirm
    )
    Divider()
}

@Composable
fun Perfil(
    appViewModel: AppViewModel,
    userViewModel: UserViewModel,
    modifier: Modifier,
    onEditProfile: () -> Unit
){
    val profilePicture: Bitmap? = userViewModel.profilePicture

    Column (
        modifier = modifier.background(color = MaterialTheme.colorScheme.tertiary),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ){
        Column (
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {

            Box(contentAlignment = Alignment.BottomEnd) {
                Box(Modifier.padding(8.dp)) {
                    if (profilePicture == null) {
                        LoadingImagePlaceholder(size = 80.dp)
                    } else {

                        Image(
                            bitmap = profilePicture.asImageBitmap(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape),
                        )
                    }
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(onClick = {
                            onEditProfile()

                        })
                ) {

                    Icon(
                        painter = painterResource(id = R.drawable.circle),

                        contentDescription = null, Modifier.size(34.dp),
                        tint = MaterialTheme.colorScheme.primary)
                    androidx.compose.material.Icon(Icons.Filled.Edit, contentDescription = null, Modifier.size(18.dp), tint = Color.White)
                }

            }

            Spacer(modifier = Modifier.height(8.dp))

            // Dos textos de ejemplo
            Text(
                text = userViewModel.currentUser.nombre,
                modifier = Modifier.padding(5.dp),
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onTertiary
            )

            Text(
                text = appViewModel.currentUser,
                modifier = Modifier.padding(5.dp),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onTertiary
            )
        }
        }
}

@Composable
private fun LoadingImagePlaceholder(size: Dp = 140.dp) {
    // Creates an `InfiniteTransition` that runs infinite child animation values.
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        // `infiniteRepeatable` repeats the specified duration-based `AnimationSpec` infinitely.
        animationSpec = infiniteRepeatable(
            // The `keyframes` animates the value by specifying multiple timestamps.
            animation = keyframes {
                // One iteration is 1000 milliseconds.
                durationMillis = 1000
                // 0.7f at the middle of an iteration.
                0.7f at 500
            },
            // When the value finishes animating from 0f to 1f, it repeats by reversing the
            // animation direction.
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    Image(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .alpha(alpha),
        painter = painterResource(id = R.drawable.start_icon),
        contentDescription = null,
        contentScale = ContentScale.Crop
    )
}

/**
 * Calendario para seleccionar la fecha en las diferentes pantallas y formularios
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Calendario(show: Boolean, onConfirm: (LocalDate) -> Unit){
    if (show){
        val state = rememberDatePickerState()
        var date by remember { mutableStateOf(LocalDate.now()) }
        DatePickerDialog(
            onDismissRequest = { onConfirm(date) },
            confirmButton = {
                Button(onClick = { onConfirm(date) }) {
                    Text(text = stringResource(id = R.string.ok))
                }
            }
        ) {
            DatePicker(state = state)
            var time = state.selectedDateMillis?:System.currentTimeMillis()
            date = Instant.ofEpochMilli(time).atZone(ZoneId.of("UTC")).toLocalDate()
        }
    }
}

/**
 * Mensaje ante la falta de datos
 */
@Composable
fun NoData(){
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Puedes personalizar el ícono de falta de datos según tus necesidades
        Icon(
            painter = painterResource(id = R.drawable.close),
            contentDescription = null, // Descripción para accesibilidad
            modifier = Modifier
                .size(120.dp)
                .padding(12.dp),
            tint = Color.Gray // Color del ícono
        )
        Text(
            text = stringResource(id = R.string.no_data),
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
        )

    }
}


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

