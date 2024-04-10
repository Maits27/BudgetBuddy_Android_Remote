package com.example.budgetbuddy.utils

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun StoragePermission(){
    val permissionState2 = rememberPermissionState(
        permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    LaunchedEffect(true){
        permissionState2.launchPermissionRequest()
    }
    val permissionState3 = rememberPermissionState(
        permission = Manifest.permission.READ_EXTERNAL_STORAGE
    )
    LaunchedEffect(true){
        permissionState3.launchPermissionRequest()
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NotificationPermission(){
    val permissionState = rememberPermissionState(
        permission = android.Manifest.permission.POST_NOTIFICATIONS
    )
    LaunchedEffect(true){
        permissionState.launchPermissionRequest()
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CalendarPermission(){
    val permissionState2 = rememberPermissionState(
        permission = Manifest.permission.READ_CALENDAR
    )
    LaunchedEffect(true){
        permissionState2.launchPermissionRequest()
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CalendarWritePermission(){
    val permissionState2 = rememberPermissionState(
        permission = Manifest.permission.WRITE_CALENDAR
    )
    LaunchedEffect(true){
        permissionState2.launchPermissionRequest()
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationPermission(){
    val permissionState2 = rememberPermissionState(
        permission = Manifest.permission.ACCESS_FINE_LOCATION
    )
    LaunchedEffect(true){
        permissionState2.launchPermissionRequest()
    }
}