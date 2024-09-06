package la.devpicon.android.grenzezwielicht.geofencing

import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.maps.model.LatLng

interface GGeofenceHelper {
    fun getGeofencingRequest(geofence: Geofence): GeofencingRequest
    fun getGeofence(id: String, latLng: LatLng, radius: Float, transitionTypes: Int): Geofence
    fun getPendingIntent(): PendingIntent
}

private const val TAG = "GeofenceHelper"

class GeofenceHelper(
    baseContext: Context
) : ContextWrapper(baseContext) {

    private var pendingIntent: PendingIntent? = null

    fun getPendingIntent(): PendingIntent {
        if (pendingIntent != null) {
            return pendingIntent as PendingIntent
        }

        pendingIntent = PendingIntent.getBroadcast(
            this,
            2607,
            Intent(this, GeofenceBroadcastReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return pendingIntent as PendingIntent
    }


    fun getGeofencingRequest(geofence: Geofence): GeofencingRequest {
        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        return geofencingRequest
    }

    fun getGeofence(
        id: String,
        latLng: LatLng,
        radius: Float,
        transitionTypes: Int
    ): Geofence = Geofence.Builder()
        .setCircularRegion(latLng.latitude, latLng.longitude, radius)
        .setRequestId(id)
        .setTransitionTypes(transitionTypes)
        .setLoiteringDelay(5000)
        .setExpirationDuration(Geofence.NEVER_EXPIRE)
        .build()

    fun getErrorString(e: Exception): String =
        if (e is ApiException) {
            when (e.statusCode) {
                GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE -> "GEOFENCE_NOT_AVAILABLE"
                GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES -> "GEOFENCE_TOO_MANY_GEOFENCES"
                GeofenceStatusCodes.GEOFENCE_REQUEST_TOO_FREQUENT -> "GEOFENCE_REQUEST_TOO_FREQUENT"
                GeofenceStatusCodes.GEOFENCE_INSUFFICIENT_LOCATION_PERMISSION -> "GEOFENCE_INSUFFICIENT_LOCATION_PERMISSION"
                GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS -> "GEOFENCE_TOO_MANY_PENDING_INTENTS"
                else -> "UNKNOWN_ERROR ${e.statusCode}"
            }
        } else {
            e.localizedMessage
        }

}