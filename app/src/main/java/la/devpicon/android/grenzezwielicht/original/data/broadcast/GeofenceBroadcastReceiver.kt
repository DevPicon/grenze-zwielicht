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
import la.devpicon.android.grenzezwielicht.original.data.broadcast.notification.createGeofenceTransitionNotification
import la.devpicon.android.grenzezwielicht.original.data.db.entity.GeofenceEntity
import la.devpicon.android.grenzezwielicht.original.data.repository.GeofenceRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import la.devpicon.android.grenzezwielicht.original.Feedback
import la.devpicon.android.grenzezwielicht.original.getTransitionType
import la.devpicon.android.grenzezwielicht.original.model.GeofenceEventEntry
import java.util.UUID
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class GeofenceBroadcastReceiver : BroadcastReceiver() {
    private val job = Job()
    private val scope = CoroutineScope(job + Dispatchers.IO)

    @Inject
    lateinit var repository: GeofenceRepository

    private fun processEvent(geofencingEvent: GeofencingEvent, context: Context) {
        val triggeringLocation = geofencingEvent.triggeringLocation
        val geofenceTransition = geofencingEvent.geofenceTransition

        geofencingEvent.triggeringGeofences?.forEach {
            val uuid = UUID.randomUUID().toString()
            scope.launch {
                repository.saveGeofenceEventEntry(
                    GeofenceEventEntry(
                        uuid = uuid,
                        transitionType = getTransitionType(geofenceTransition),
                        latitude = triggeringLocation?.latitude?.toString().orEmpty(),
                        longitude = triggeringLocation?.longitude?.toString().orEmpty(),
                        geofenceSource = "${it.requestId} - ${it.transitionTypes} - ${it.latitude} ${it.longitude} ${it.radius}",
                        feedback = Feedback.UNDEFINED,
                        brand = Build.BRAND,
                        model = Build.MODEL,
                        osVersion = Build.VERSION.RELEASE,
                        appVersion = getAppVersion(context)
                    )
                )

                if (it.requestId.toIntOrNull() == null) {
                    Toast.makeText(context, "Request Id was null", Toast.LENGTH_SHORT).show()
                }

                val storedGeofence = repository.getGeofence(it.requestId) ?: GeofenceEntity(
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
                    geofence = storedGeofence
                )

            }


        }
    }

    private fun showNotification(
        context: Context,
        transitionType: Int,
        transitionUuid: String,
        geofence: GeofenceEntity
    ) {
        val notificationFeedbackId = Random.nextInt(Int.MAX_VALUE)
        val notification = createGeofenceTransitionNotification(
            context,
            transitionType,
            transitionUuid,
            notificationFeedbackId,
            geofence
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