package com.example.budgetbuddy

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import com.example.budgetbuddy.VM.AppViewModel
import com.example.budgetbuddy.VM.PreferencesViewModel
import com.example.budgetbuddy.VM.UserViewModel
import com.example.budgetbuddy.screens.LoginPage
import com.example.budgetbuddy.ui.theme.BudgetBuddyTheme
import com.example.budgetbuddy2.screens.MainView
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDate
import java.util.UUID

/************************************************
 ****              Main Activity             ****
 ***********************************************/
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    /**
     * View models e ID del canal para las notificaciones.
     */
    val userViewModel by viewModels<UserViewModel> ()

    val appViewModel by viewModels<AppViewModel> ()
    val preferencesViewModel by viewModels<PreferencesViewModel> ()
    companion object{
        const val CHANNEL_ID = "BudgetBuddy"
    }

    /**
     * Métodos principales de la actividad relativos al ciclo de vida
     */

    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Creación del canal de notificación
        createNotificationChannel()
        setContent {
            BudgetBuddyTheme(preferencesViewModel = preferencesViewModel) {
                // Método para la descarga de ficheros
                val guardarFichero: ( LocalDate, String)-> Boolean = { fecha, datos ->
                    guardarDatosEnArchivo(appViewModel, fecha, datos) }
                preferencesViewModel.restartLang(preferencesViewModel.idioma.collectAsState(initial = preferencesViewModel.currentSetLang).value)
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Solicitud de permisos
                    NotificationPermission()
                    StoragePermission()
//                    MainView(
//                        appViewModel = appViewModel,
//                        preferencesViewModel = preferencesViewModel,
//                        guardarFichero
//                    )
//                    LoginPage()
                    MyApp(
                        userViewModel = userViewModel,
                        appViewModel = appViewModel,
                        preferencesViewModel = preferencesViewModel,
                        guardarFichero
                    )
                }
            }
        }

    }

    /**
     * Descargar contenido String a fichero TXT (Requisito opcional)
     * Código de: https://www.geeksforgeeks.org/android-jetpack-compose-external-storage/
     */
    fun guardarDatosEnArchivo(
        appViewModel: AppViewModel,
        fecha: LocalDate,
        datos:String
    ): Boolean{
        val nombre = appViewModel.fecha_txt(fecha)
        ActivityCompat.requestPermissions(
            this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 23
        )
        val folder: File =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(folder, "Factura${nombre}_${UUID.randomUUID()}.txt")
//        if (file.exists()) {
//            if (!file.delete()) {
//                Log.d("DOWNLOAD WARNING", "No se pudo eliminar el archivo existente.")
//                return false
//            }
//        }
        return writeTextData(file, datos)
    }
    fun writeTextData(
        file: File,
        data: String
    ):Boolean {
        var fileOutputStream: FileOutputStream? = null
        try {
            fileOutputStream = FileOutputStream(file)
            fileOutputStream.write(data.toByteArray())
        } catch (e: Exception) {
            Log.d("DOWNLOAD WARNING", "1: $e")
            return false
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close()
                } catch (e: IOException) {
                    Log.d("DOWNLOAD WARNING", "2: $e")
                    return false
                }
            }
        }
        return true
    }

    /**
     * Método de creación del canal:
     * Android Developers: https://developer.android.com/develop/ui/views/notifications/build-notification?hl=es-419
     */
    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = getString(R.string.channel_description)
            }

            // Register the channel with the system.
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun NotificationPermission(){
        val permissionState = rememberPermissionState(
            permission = android.Manifest.permission.POST_NOTIFICATIONS
        )
        LaunchedEffect(true){
            permissionState.launchPermissionRequest()
        }
    }
    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun StoragePermission(){
        val permissionState2 = rememberPermissionState(
            permission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        LaunchedEffect(true){
            permissionState2.launchPermissionRequest()
        }
    }

}
