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
import java.util.UUID

private const val TAG = "GeofenceHelper"

class GeofenceHelper(
    baseContext: Context
) : ContextWrapper(baseContext) {

    fun getPendingIntent(): PendingIntent  = PendingIntent.getBroadcast(
            this,
            0,
            Intent(this, GeofenceBroadcastReceiver::class.java),
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )


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
        .setRequestId(UUID.randomUUID().toString())
        .setCircularRegion(latLng.latitude, latLng.longitude, radius)
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