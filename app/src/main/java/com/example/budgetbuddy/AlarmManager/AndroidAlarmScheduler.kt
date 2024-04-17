package com.example.budgetbuddy.AlarmManager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.budgetbuddy.Local.Data.AlarmItem
import java.time.LocalDateTime
import java.time.ZoneId


/**
 * Clase que implementa la interfaz de [AlarmScheduler]
 * Utiliza los métodos de [schedule] y [cancel] para gestionar las alarmas del servicio.
 */
/**             (Requisito opcional)           **/
class AndroidAlarmScheduler (
    private val context: Context,
): AlarmScheduler{

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun schedule(item: AlarmItem, logout: String) {
        if (logout==""){
            if (item.time > LocalDateTime.now()){
                val intent = Intent(context, AlarmReceiver::class.java).apply {
                    putExtra("TITLE", item.title)
                    putExtra("BODY", item.body)
                    putExtra("LOGOUT", logout)
                }
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    item.time.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000,
                    PendingIntent.getBroadcast(
                        context,
                        item.hashCode(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                )
            }
        }else{
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("TITLE", item.title)
                putExtra("BODY", item.body)
                putExtra("LOGOUT", logout)
            }
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                item.time.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000,
                PendingIntent.getBroadcast(
                    context,
                    logout.hashCode(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
        }
    }

    override fun cancel(item: AlarmItem) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
            context,
            item.hashCode(),
            Intent(context, AlarmReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        ))
    }

}