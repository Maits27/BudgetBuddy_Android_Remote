package com.example.budgetbuddy.AlarmManager

import com.example.budgetbuddy.Data.Room.AlarmItem

interface AlarmScheduler {
    fun schedule(item: AlarmItem)
    fun cancel(item: AlarmItem)
}