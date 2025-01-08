package la.devpicon.android.grenzezwielicht.original.data.broadcast

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import com.google.android.gms.location.GeofencingEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import la.devpicon.android.grenzezwielicht.original.Feedback
import la.devpicon.android.grenzezwielicht.original.data.broadcast.notification.createGeofenceTransitionNotification
import la.devpicon.android.grenzezwielicht.original.data.db.entity.GeofenceEntity
import la.devpicon.android.grenzezwielicht.original.data.repository.GeofenceRepository
import la.devpicon.android.grenzezwielicht.original.getTransitionType
import la.devpicon.android.grenzezwielicht.original.model.GeofenceEventEntry
import java.util.UUID
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class GeofenceBroadcastReceiver : BroadcastReceiver() {
    /**
     * Manages the lifecycle of coroutines launched by this receiver
     */
    private val job = Job()

    /**
     * Coroutine
     */
    private val scope = CoroutineScope(job + Dispatchers.IO)

    @Inject
    lateinit var repository: GeofenceRepository

    /**
     * Processes a geofence event with guaranteed notification delivery
     * @param geofencingEvent the event containing the geofence transition information
     * @param context application context for system services
     */
    private fun processEvent(geofencingEvent: GeofencingEvent, context: Context) {
        val triggeringLocation = geofencingEvent.triggeringLocation
        val geofenceTransition = geofencingEvent.geofenceTransition
        val eventTime = System.currentTimeMillis() // Capture the event time once

        // Use goAsync()
        val pendingResult = goAsync()

        geofencingEvent.triggeringGeofences?.forEach { geofence ->
            val uuid = UUID.randomUUID().toString()
            scope.launch {
                try {
                    repository.saveGeofenceEventEntry(
                        GeofenceEventEntry(
                            uuid = uuid,
                            transitionType = getTransitionType(geofenceTransition),
                            timestamp = eventTime,
                            latitude = triggeringLocation?.latitude?.toString().orEmpty(),
                            longitude = triggeringLocation?.longitude?.toString().orEmpty(),
                            geofenceSource = "${geofence.requestId} - ${geofence.transitionTypes} - ${geofence.latitude} ${geofence.longitude} ${geofence.radius}",
                            feedback = Feedback.UNDEFINED,
                            brand = Build.BRAND,
                            model = Build.MODEL,
                            osVersion = Build.VERSION.RELEASE,
                            appVersion = getAppVersion(context)
                        )
                    )

                    if (geofence.requestId.toIntOrNull() == null) {
                        Toast.makeText(context, "Request Id was null", Toast.LENGTH_SHORT).show()
                    }
                    val storedGeofence =
                        repository.getGeofence(geofence.requestId) ?: GeofenceEntity(
                            id = 0,
                            latitude = 0.0f,
                            longitude = 0.0f,
                            label = "Error",
                            radius = 0f
                        )
                    showNotification(
                        context = context,
                        transitionType = geofenceTransition,
                        transitionUuid = uuid,
                        geofence = storedGeofence,
                        timestamp = eventTime // Pass
                    )
                } catch (e: Exception) {
                    android.util.Log.e(
                        "GeofenceBroadcastReceiver",
                        "Failed to process geofence transition",
                        e
                    )
                } finally {
                    // If this is the last geofence being processed, complete the broadcast
                    if (geofence == geofencingEvent.triggeringGeofences?.last()) {
                        pendingResult.finish()
                    }
                }
            }
        }
    }

    private fun showNotification(
        context: Context,
        transitionType: Int,
        transitionUuid: String,
        geofence: GeofenceEntity,
        timestamp: Long
    ) {
        val notificationFeedbackId = Random.nextInt(Int.MAX_VALUE)
        val notification = createGeofenceTransitionNotification(
            context,
            transitionType,
            transitionUuid,
            notificationFeedbackId,
            geofence,
            timestamp
        )
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationFeedbackId, notification)
    }

    override fun onReceive(context: Context, intent: Intent) {

        val geofencingEvent = intent.let { GeofencingEvent.fromIntent(it) }

        if (geofencingEvent == null) {
            return
        }

        if (geofencingEvent.hasError()) {
            return
        }

        // Get transition types
        processEvent(
            geofencingEvent = geofencingEvent,
            context = context
        )
    }

    private fun getAppVersion(context: Context): String {
        return try {
            val packageInfo: PackageInfo =
                context.packageManager.getPackageInfo(context.packageName, 0)
            val versionName = packageInfo.versionName // e.g., "1.0"
            val versionCode = packageInfo.longVersionCode // e.g., 1 (API 28+)
            "Version: $versionName (Code: $versionCode)"
        } catch (e: PackageManager.NameNotFoundException) {
            "Version not found ${e.message}"
        }
    }
}