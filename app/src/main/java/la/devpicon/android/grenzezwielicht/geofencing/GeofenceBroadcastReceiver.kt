package la.devpicon.android.grenzezwielicht.geofencing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

private const val TAG = "GeofenceBroadcastReceiver"

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        //Toast.makeText(context, "Geofence triggered...", Toast.LENGTH_SHORT).show()

        val notificationHelper = NotificationHelper(context)

        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        if(geofencingEvent == null){
            Log.d(TAG, "onReceive: Error receiving geofence event, event should not be null...")
            return

        }

        if (geofencingEvent.hasError() == true) {
            Log.d(TAG, "onReceive: Error receiving geofence event...")
            // Handle error
            return
        }

        val geofenceList = geofencingEvent.triggeringGeofences ?: listOf()
        geofenceList.forEach { Log.d(TAG, "onReceive: ${it.requestId}") }

        // Get the transition types
        val geofenceTransitionType = geofencingEvent.geofenceTransition

        when (geofenceTransitionType) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                Toast.makeText(
                    context,
                    "Geofence.GEOFENCE_TRANSITION_ENTER",
                    Toast.LENGTH_SHORT
                ).show()
                notificationHelper.sendHighPriorityNotification(
                    "Geofence.GEOFENCE_TRANSITION_ENTER",
                    "",
                    MapsActivity::class.java
                )
            }

            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                Toast.makeText(
                    context,
                    "Geofence.GEOFENCE_TRANSITION_EXIT",
                    Toast.LENGTH_SHORT
                )
                    .show()
                notificationHelper.sendHighPriorityNotification(
                    "Geofence.GEOFENCE_TRANSITION_EXIT",
                    "",
                    MapsActivity::class.java
                )
            }

            Geofence.GEOFENCE_TRANSITION_DWELL -> {
                Toast.makeText(
                    context,
                    "Geofence.GEOFENCE_TRANSITION_DWELL",
                    Toast.LENGTH_SHORT
                )
                    .show()
                notificationHelper.sendHighPriorityNotification(
                    "Geofence.GEOFENCE_TRANSITION_DWELL",
                    "",
                    MapsActivity::class.java
                )
            }

            else -> Toast.makeText(context, "Unknown type $geofenceTransitionType", Toast.LENGTH_SHORT).show()
        }
    }
}

