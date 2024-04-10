package com.example.budgetbuddy

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import com.example.budgetbuddy.VM.AppViewModel
import com.example.budgetbuddy.VM.PreferencesViewModel
import com.example.budgetbuddy.VM.UserViewModel
import com.example.budgetbuddy.utils.CalendarPermission
import com.example.budgetbuddy.utils.StoragePermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    val pickMedia = registerForActivityResult(PickVisualMedia()){
        if (it!=null){
            var ivImage = ImageView(this)
            ivImage.setImageURI(it)
            val drawable: Drawable = ivImage.drawable

            // Si el drawable es una instancia de BitmapDrawable, obtener el Bitmap directamente
            if (drawable is BitmapDrawable) {
                userViewModel.setProfileImage(appViewModel.currentUser, drawable.bitmap)
            }
        }else{
            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
        }
    }
    
    companion object{
        const val CHANNEL_ID = "BudgetBuddy"
    }

    /**
     * Métodos principales de la actividad relativos al ciclo de vida
     */

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Creación del canal de notificación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createNotificationChannel()
        setContent {
                // Método para la descarga de ficheros
                val guardarFichero: ( LocalDate, String)-> Boolean = { fecha, datos ->
                    guardarDatosEnArchivo(appViewModel, fecha, datos) }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Solicitud de permisos
                    CalendarPermission()
                    StoragePermission()
                    MyApp(
                        userViewModel = userViewModel,
                        appViewModel = appViewModel,
                        preferencesViewModel = preferencesViewModel,
                        fusedLocationClient = fusedLocationClient,
                        pickMedia = pickMedia,
                        guardarFichero
                    )
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



}

