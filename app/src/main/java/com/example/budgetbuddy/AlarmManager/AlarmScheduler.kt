package com.example.budgetbuddy.AlarmManager

import com.example.budgetbuddy.Local.Data.AlarmItem


/**
 * Interfaz con los métodos para gestionar el [AndroidAlarmScheduler]
 */
/**             (Requisito opcional)           **/
interface AlarmScheduler {
    fun schedule(item: AlarmItem, logout: String = "")
    fun cancel(item: AlarmItem)
}