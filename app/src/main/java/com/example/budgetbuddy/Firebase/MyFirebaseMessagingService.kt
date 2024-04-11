package com.example.budgetbuddy.Firebase

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.budgetbuddy.MainActivity
import com.example.budgetbuddy.R
import com.example.budgetbuddy.utils.NotificationPermission
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        remoteMessage.notification?.let { notification ->

            Log.d("FCM", "Message Notification Title: ${notification.title}")
            Log.d("FCM", "Message Notification Body: ${notification.body}")

            // Show user created notification
            val builder = NotificationCompat.Builder(this, MainActivity.CHANNEL_ID)
                .setSmallIcon(R.drawable.infor)
                .setContentTitle(notification.title)
                .setContentText(notification.body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)

            with(NotificationManagerCompat.from(this)) {
                if (ActivityCompat.checkSelfPermission(
                        this@MyFirebaseMessagingService,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    notify(MainActivity.FIREBASE_NOTIFICATION, builder.build())
                }
            }

        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Handle token refresh here, you might need to send it to your server
    }
}
