package com.example.budgetbuddy.AlarmManager

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.impl.utils.ForceStopRunnable.BroadcastReceiver
import com.example.budgetbuddy.Data.Remote.HTTPService
import com.example.budgetbuddy.MainActivity
import com.example.budgetbuddy.R
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("RestrictedApi")
class AlarmReceiver: BroadcastReceiver() {
    @Inject
    lateinit var httpClient: HTTPService

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("RestrictedApi", "ServiceCast")
    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("ALARM", "ALARMA INTENT: $intent")

        val title = intent?.getStringExtra("TITLE")?: return
        val body = intent.getStringExtra("BODY") ?: return
        println("Alarm triggered $body")

        val notificationId = System.currentTimeMillis().toInt()
        Log.d("ALARM", "ALARMA ID: $notificationId")

        // Crear un NotificationManager
        val builder = NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
            .setSmallIcon(R.drawable.infor)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
        Log.d("ALARM", "BUILDER")
        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("ALARM", "ALARMA ENVIADA")
                notify(notificationId, builder.build())
            }
        }
//        GlobalScope.launch(Dispatchers.IO) {
//            httpClient.sendNotificationToAll("{" +
//                    "'title': '$tittle'," +
//                    "'body': '$body'" +
//                    "}")
//        }
    }
}