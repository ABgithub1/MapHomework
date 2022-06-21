package com.example.maphomework.ui.map

import android.Manifest
import android.content.Context
import android.location.Location
import android.os.Looper
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LocationService(context: Context) {

    private val locationClient = LocationServices.getFusedLocationProviderClient(context)

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    suspend fun getCurrentLocation(): Location? = suspendCoroutine { continuation ->
        locationClient.lastLocation
            .addOnSuccessListener { location ->
                continuation.resume(location)
            }
            .addOnCanceledListener {
                continuation.resume(null)
            }
            .addOnFailureListener {
                continuation.resume(null)
            }
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun getLocationFlow() = callbackFlow {

        val locationRequest = LocationRequest.create().apply {
            interval = 5000
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                trySend(result.lastLocation)
            }
        }
        locationClient
            .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())

        awaitClose {
            locationClient.removeLocationUpdates(locationCallback)
        }
    }.distinctUntilChanged { old, new ->
        old?.latitude != new?.latitude || old?.longitude != new?.longitude
    }

}