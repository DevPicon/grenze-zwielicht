package la.devpicon.android.grenzezwielicht.original.data.location

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.location.Location
import android.os.Build
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_DWELL
import com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER
import com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_EXIT
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await
import la.devpicon.android.grenzezwielicht.original.data.broadcast.GeofenceBroadcastReceiver

class GeofenceHelper(context: Context) : ContextWrapper(context) {
    private val client = LocationServices.getGeofencingClient(context)
    private val geofenceList = mutableMapOf<String, Geofence>()

    private val geofencingPendingIntent by lazy {
        PendingIntent.getBroadcast(
            this,
            0,
            Intent(this, GeofenceBroadcastReceiver::class.java),
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_MUTABLE
            }
        )
    }

    fun addGeofence(
        key: String,
        location: Location,
        radiusInMeters: Float = 30f
    ) {
        geofenceList[key] = createGeofence(key, location, radiusInMeters)
    }

    fun removeGeofence(key: String) = geofenceList.remove(key)

    @SuppressLint("MissingPermission")
    fun registerGeofence(
        onSuccess: (() -> Unit)? = null,
        onFailure: (() -> Unit)? = null
    ) {
        client.addGeofences(createGeofencingRequest(), geofencingPendingIntent)
            .addOnSuccessListener {
                Toast.makeText(baseContext, "Geofence added", Toast.LENGTH_SHORT).show()
                onSuccess?.invoke()
            }
            .addOnFailureListener {
                Toast.makeText(baseContext, "Geofence add failed", Toast.LENGTH_SHORT).show()
                onFailure?.invoke()
            }
    }

    suspend fun deregisterGeofence() = kotlin.runCatching {
        client.removeGeofences(geofencingPendingIntent).await()
    }

    private val transitions =
        GEOFENCE_TRANSITION_ENTER or GEOFENCE_TRANSITION_DWELL or GEOFENCE_TRANSITION_EXIT

    private fun createGeofencingRequest(): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(transitions)
            addGeofences(geofenceList.values.toList())
        }.build()
    }

    private fun createGeofence(key: String, location: Location, radiusInMeeters: Float): Geofence {
        return Geofence.Builder()
            .setRequestId(key)
            .setCircularRegion(location.latitude, location.longitude, radiusInMeeters)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setLoiteringDelay(5000)
            .setTransitionTypes(transitions)
            .build()
    }
}