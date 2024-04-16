package com.example.budgetbuddy.Shared

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.example.budgetbuddy.MainActivity
import com.example.budgetbuddy.R

/***************************************
 **  Notificaciones de la aplicación  **
 ***************************************/

// Aquí se implementan todas las notificaciones de la aplicación.
// Además, se implementan los intent implícitos para interactuar con otras aplicaciones del teléfono.

/**
 * Notificación de aviso de descarga, informando de la localización del archivo (Requisito 4).
 */
fun downloadNotification(
    context: Context,
    titulo: String,
    description: String,
    id: Int
){
    val notificationManager = context.getSystemService(NotificationManager::class.java)

    var notification = NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
        .setContentTitle(titulo)
        .setContentText(description)
        .setSmallIcon(R.drawable.download)
        .setAutoCancel(true)
        .setStyle(
            NotificationCompat.BigTextStyle()
            .bigText(description))
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .build()

    notificationManager.notify(id, notification)
}

/**
 * Mediante intents implícitos genera un intent de texto plano
 * o una acción de envio por email, según los parámetros que
 * reciba o las características de estos.
 *
 * También se encarga, en caso de envio por email, de preparar
 * una estructura predefinida para este con los parámetros de
 * [asunto] y [contenido].
 *
 * (Requisito opcional)
 */

fun compartirContenido(
    context: Context,
    contenido: String,
    uri: String= "",
    asunto: String = ""
){
    if (uri=="" && asunto==""){
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, contenido)
            type = "text/plain"
            putExtra(Intent.EXTRA_TITLE, "BudgetBuddy")
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)

    }else{
        val destinatario = "budgetbuddy46@gmail.com"

        val uriString = "mailto:" + Uri.encode(destinatario) +
                "?subject=" + Uri.encode(asunto) +
                "&body=" + Uri.encode(contenido)
        val uri = Uri.parse(uriString)
        val intent = Intent(Intent.ACTION_SENDTO, uri)
        context.startActivity(intent)
    }

}



