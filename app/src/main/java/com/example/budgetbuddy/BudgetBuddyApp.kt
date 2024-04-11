package com.example.budgetbuddy

import android.Manifest
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import dagger.hilt.android.HiltAndroidApp



/*******************************************************************************
 ****                          Aplication Class                             ****
 *******************************************************************************/

/**
Necesaria para el Framework de Hilt.
**/

@HiltAndroidApp
class BudgetBuddyApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
