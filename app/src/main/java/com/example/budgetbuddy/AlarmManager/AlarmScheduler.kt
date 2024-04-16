package com.example.budgetbuddy.AlarmManager

import com.example.budgetbuddy.Local.Data.AlarmItem

interface AlarmScheduler {
    fun schedule(item: AlarmItem)
    fun cancel(item: AlarmItem)
}