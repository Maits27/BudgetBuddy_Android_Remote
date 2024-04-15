package com.example.budgetbuddy.AlarmManager

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.impl.utils.ForceStopRunnable.BroadcastReceiver
import com.example.budgetbuddy.Data.Remote.HTTPService
import com.example.budgetbuddy.MainActivity
import com.example.budgetbuddy.R
import javax.inject.Inject

@SuppressLint("RestrictedApi")
class AlarmReceiver: BroadcastReceiver() {
    @Inject
    lateinit var httpClient: HTTPService

    @SuppressLint("RestrictedApi", "ServiceCast")
    override fun onReceive(context: Context, intent: Intent?) {

        val title = intent?.getStringExtra("TITLE")?: return
        val body = intent.getStringExtra("BODY") ?: return

        val notificationId = System.currentTimeMillis().toInt()

        // Crear un NotificationManager
        val builder = NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
            .setSmallIcon(R.drawable.infor)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notify(notificationId, builder.build())
            }
        }
    }
}