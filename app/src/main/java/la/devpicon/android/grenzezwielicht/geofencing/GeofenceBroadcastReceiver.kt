package la.devpicon.android.grenzezwielicht.geofencing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

private const val TAG = "GeofenceBroadcastReceiver"

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {

        val geofencingEvent = intent?.let { GeofencingEvent.fromIntent(it) }

        if (geofencingEvent == null) {
            Log.d(TAG, "onReceive: Error receiving geofence event, event should not be null...")
            return

        }

        if (geofencingEvent.hasError()) {
            val errorMessage = GeofenceStatusCodes
                .getStatusCodeString(geofencingEvent.errorCode)
            Log.e(TAG, "onReceive: Error receiving geofence event...")
            // Handle error
            return
        }

        val geofenceList = geofencingEvent.triggeringGeofences ?: listOf()
        geofenceList.forEach { Log.d(TAG, "onReceive: ${it.requestId}") }

        // Get the transition types
        val geofenceTransitionType = geofencingEvent.geofenceTransition

        val transitionType = when (geofenceTransitionType) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                Log.i(TAG, "TransitionType: Enter to geofence")
                "Geofence.GEOFENCE_TRANSITION_ENTER"
            }

            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                Log.i(TAG, "TransitionType: Exit from geofence")
                "Geofence.GEOFENCE_TRANSITION_EXIT"
            }

            Geofence.GEOFENCE_TRANSITION_DWELL -> {
                Log.i(TAG, "TransitionType: In geofence")
                "Geofence.GEOFENCE_TRANSITION_DWELL"
            }

            else -> "Unknown type $geofenceTransitionType"
        }

        val notificationIntent = Intent(context, MapsActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val notificationHelper = NotificationHelper(context)
        notificationHelper.sendHighPriorityNotification(
            title = transitionType,
            body = "This is the content of my notification",
            notificationIntent
        )
    }
}

