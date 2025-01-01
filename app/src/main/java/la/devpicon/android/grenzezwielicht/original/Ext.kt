package la.devpicon.android.grenzezwielicht.original

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.LocationServices

const val TAG = "Geofence POC - "

// Notification action constants
const val ACTION_THUMB_UP = "ACTION_THUMB_UP"
const val ACTION_THUMB_DOWN = "ACTION_THUMB_DOWN"
const val EXTRA_TRANSITION_TYPE = "transition_type"
const val EXTRA_NOTIFICATION_ID = "notification_id"
const val EXTRA_TRANSITION_UUID = "transition_uuid"

const val ACTION_LOCATION_UPDATE = "ACTION_LOCATION_UPDATE"
const val EXTRA_LOCATION = "EXTRA_LOCATION"

// Notification channel constants
const val GEOFENCE_POC_CHANNEL_ID = "location_service_channel"
const val GEOFENCE_POC_CHANNEL_NAME = "Location Service"

// Notification channel constants for Geofence Events
const val GEOFENCE_EVENTS_CHANNEL_ID = "geofence_events_service_channel"
const val GEOFENCE_EVENTS_CHANNEL_NAME = "Geofence events"

// Notification constants
const val NOTIFICATION_LOCATION_ID = 1231
const val NOTIFICATION_FEEDBACK_ID = 37428

// Location update constants
const val LOCATION_UPDATE_INTERVAL = 60000L // 10s
const val LOCATION_FASTEST_INTERVAL = 30000L // 5s

fun getTransitionType(geofenceTransition: Int): String = when (geofenceTransition) {
    Geofence.GEOFENCE_TRANSITION_ENTER -> "GEOFENCE_TRANSITION_ENTER"
    Geofence.GEOFENCE_TRANSITION_DWELL -> "GEOFENCE_TRANSITION_DWELL"
    Geofence.GEOFENCE_TRANSITION_EXIT -> "GEOFENCE_TRANSITION_EXIT"
    else -> "other"
}

enum class Feedback(val value: Int) {
    UNDEFINED(-1),
    GOOD(0),
    BAD(1)
}


@SuppressLint("MissingPermission") // Ensure permissions are checked before calling this function
fun getCurrentLocation(context: Context, onLocationResult: (Location?) -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    fusedLocationClient.lastLocation
        .addOnSuccessListener { location: Location? ->
            onLocationResult(location)
        }
        .addOnFailureListener { _ ->
            onLocationResult(null)
        }
}