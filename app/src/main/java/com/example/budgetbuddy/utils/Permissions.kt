package com.example.budgetbuddy.utils

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState


/***************************************************
 ***          Permisos de toda la App            ***
 ***************************************************/
@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun AskPermissions(){
    val permissions = arrayOf(
        Manifest.permission.POST_NOTIFICATIONS,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_CALENDAR,
        Manifest.permission.WRITE_CALENDAR,
    )
    val permissionState = rememberMultiplePermissionsState(
        permissions = permissions.toList()

    )
    LaunchedEffect(true){
        permissionState.launchMultiplePermissionRequest()
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