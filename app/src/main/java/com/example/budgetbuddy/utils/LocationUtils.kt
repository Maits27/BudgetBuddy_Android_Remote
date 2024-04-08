package com.example.budgetbuddy.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewModelScope
import com.example.budgetbuddy.screens.LocationPermission
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

suspend fun obtenerUbicacionActual(
    fusedLocationClient:FusedLocationProviderClient,
    context: Context
): Location? {
    try {
        // Intenta obtener la ubicación actual utilizando fusedLocationClient.lastLocation.result

        if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
        ) {
            return null
        }
        return fusedLocationClient.lastLocation.result
    } catch (e: Exception) {
        // Manejar cualquier error aquí
        return null
    }
}

fun currentLocation(
    context: Context,
    fusedLocationClient: FusedLocationProviderClient
): Location?{
    var loc: Location? = null
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        Log.d("LOCATION", "LOCATION ERROR 1")
        return loc
    }
    return fusedLocationClient.lastLocation.result

}