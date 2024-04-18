package com.example.budgetbuddy.AlarmManager

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.impl.utils.ForceStopRunnable.BroadcastReceiver
import com.example.budgetbuddy.Remote.HTTPService
import com.example.budgetbuddy.MainActivity
import com.example.budgetbuddy.R
import com.example.budgetbuddy.Repositories.ILoginSettings
import com.example.budgetbuddy.Repositories.IUserRepository
import com.example.budgetbuddy.Repositories.UserRepository
import com.example.budgetbuddy.VM.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * Recoge las alarmas programadas por el [AlarmScheduler]
 * y ejecuta el código necesario en base a lo programado.
 */
/**             (Requisito opcional)           **/
@AndroidEntryPoint
@SuppressLint("RestrictedApi")
class AlarmReceiver : BroadcastReceiver() {
    val httpService = HTTPService()
    private val coroutineScope = MainScope()
    @Inject
    lateinit var userRepository: ILoginSettings

    @SuppressLint("RestrictedApi")
    override fun onReceive(context: Context, intent: Intent?) {


        val title = intent?.getStringExtra("TITLE")?: return
        val body = intent.getStringExtra("BODY") ?: return
        val logout = intent.getStringExtra("LOGOUT") ?: return

        val notificationId = System.currentTimeMillis().toInt()
        if (title!="" || body!=""){

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
        }else{
//            val builder = NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
//                .setSmallIcon(R.drawable.logout)
//                .setContentTitle(logout)
//                .setContentText("Logout after 1h")
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setAutoCancel(true)
//
//            with(NotificationManagerCompat.from(context)) {
//                if (ActivityCompat.checkSelfPermission(
//                        context,
//                        Manifest.permission.POST_NOTIFICATIONS
//                    ) == PackageManager.PERMISSION_GRANTED
//                ) {
//                    notify(notificationId, builder.build())
//                }
//            }

//            coroutineScope.launch {
//                userRepository.setLastLoggedUser("")
//                httpService.loginUser(logout, false)
//            }
        }
    }
}

