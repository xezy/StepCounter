package com.stepcounter.core.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sqrt

data class GpsFix(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val speed: Float,
    val timestamp: Long
)

class LocationTracker(context: Context) {
    private val client = FusedLocationProviderClient(context)

    private val _locationUpdates = MutableSharedFlow<GpsFix>(extraBufferCapacity = 1)
    val locationUpdates = _locationUpdates.asSharedFlow()

    private val locationRequest = LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 60000L)
        .setMinUpdateIntervalMillis(60000L)
        .build()

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.lastLocation?.let { location ->
                if (isValidFix(location)) {
                    val fix = GpsFix(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        accuracy = location.accuracy,
                        speed = location.speed,
                        timestamp = location.time
                    )
                    _locationUpdates.tryEmit(fix)
                }
            }
        }
    }

    fun isValidFix(location: Location): Boolean {
        if (location.accuracy > 20f) return false
        if (location.speed > 5.5f) return false
        return true
    }

    @SuppressLint("MissingPermission")
    fun startTracking() {
        try {
            client.requestLocationUpdates(locationRequest, locationCallback, null)
        } catch (e: SecurityException) {
            return
        }
    }

    fun stopTracking() {
        client.removeLocationUpdates(locationCallback)
    }
}
