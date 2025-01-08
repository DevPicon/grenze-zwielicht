package la.devpicon.android.grenzezwielicht.original.data.broadcast.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.Geofence
import la.devpicon.android.grenzezwielicht.R
import la.devpicon.android.grenzezwielicht.original.ACTION_THUMB_DOWN
import la.devpicon.android.grenzezwielicht.original.ACTION_THUMB_UP
import la.devpicon.android.grenzezwielicht.original.EXTRA_NOTIFICATION_ID
import la.devpicon.android.grenzezwielicht.original.EXTRA_TRANSITION_TYPE
import la.devpicon.android.grenzezwielicht.original.EXTRA_TRANSITION_UUID
import la.devpicon.android.grenzezwielicht.original.GEOFENCE_EVENTS_CHANNEL_ID
import la.devpicon.android.grenzezwielicht.original.GEOFENCE_EVENTS_CHANNEL_NAME
import la.devpicon.android.grenzezwielicht.original.GeofencePocActivity
import la.devpicon.android.grenzezwielicht.original.data.db.entity.GeofenceEntity
import la.devpicon.android.grenzezwielicht.original.userfeedback.NotificationFeedbackActionReceiver
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Creates a notification for a geofence transition event with detailed timestamp information
 *
 * @param context Application context used for system services and resources
 * @param transitionType The type of geofence transition (ENTER, EXIT, or DWELL)
 * @param transitionUuid Unique identifier for tracking this specific transition
 * @param notificationFeedbackId Identifier for managing notification feedback actions
 * @param geofence The geofence entity containing location details
 * @param timestamp The exact time when this transition occurred
 * @return A fully configured notification ready to be displayed
 */
fun createGeofenceTransitionNotification(
    context: Context,
    transitionType: Int,
    transitionUuid: String,
    notificationFeedbackId: Int,
    geofence: GeofenceEntity,
    timestamp: Long
): Notification {
    // Setup the notification channel for geofence events
    val geofenceEventsChannelId = GEOFENCE_EVENTS_CHANNEL_ID
    val geofenceEventsChannelName = GEOFENCE_EVENTS_CHANNEL_NAME

    val notificationChannel = NotificationChannel(
        geofenceEventsChannelId,
        geofenceEventsChannelName,
        NotificationManager.IMPORTANCE_DEFAULT
    ).apply {
        description = "Channel for geofence transitions"
    }

    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(notificationChannel)

    // Create a human-readable timestamp
    val formattedTime = DateTimeFormatter
        .ofPattern("MMM dd, yyyy HH:mm:ss")
        .format(
            Instant.ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime())

    val title = when (transitionType) {
        Geofence.GEOFENCE_TRANSITION_ENTER -> "Entered into ${geofence.label}"
        Geofence.GEOFENCE_TRANSITION_DWELL -> "Staying in ${geofence.label}"
        Geofence.GEOFENCE_TRANSITION_EXIT -> "Exited from ${geofence.label}"
        else -> "Other"
    }

    // Create the content text with timestamp
    val contentText = "At $formattedTime\nPlease give us your feedback"

    // Set up feedback actions (thumbs up)
    val thumbUpIntent = Intent(context, NotificationFeedbackActionReceiver::class.java)
        .apply {
            action = ACTION_THUMB_UP
            putExtra(EXTRA_TRANSITION_TYPE, transitionType)
            putExtra(EXTRA_NOTIFICATION_ID, notificationFeedbackId)
            putExtra(EXTRA_TRANSITION_UUID, transitionUuid)
        }

    val thumbUpPendingIntent = PendingIntent.getBroadcast(
        context, notificationFeedbackId, thumbUpIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Set up feedback actions (thumbs down)
    val thumbDownIntent = Intent(context, NotificationFeedbackActionReceiver::class.java)
        .apply {
            action = ACTION_THUMB_DOWN
            putExtra(EXTRA_TRANSITION_TYPE, transitionType)
            putExtra(EXTRA_NOTIFICATION_ID, notificationFeedbackId)
            putExtra(EXTRA_TRANSITION_UUID, transitionUuid)
        }

    val thumbDownPendingIntent = PendingIntent.getBroadcast(
        context, notificationFeedbackId, thumbDownIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Set up the intent for opening the app
    val openAppIntent = Intent(context, GeofencePocActivity::class.java)
    val openAppPendingIntent = PendingIntent.getActivity(
        context, notificationFeedbackId, openAppIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    return NotificationCompat.Builder(context, geofenceEventsChannelId)
        .setSmallIcon(R.drawable.baseline_share_location_24)
        .setContentTitle(title)
        .setContentText(contentText)
        .setStyle(NotificationCompat.BigTextStyle().bigText(contentText)) // Enable expanded text view
        .setContentIntent(openAppPendingIntent)
        .setAutoCancel(true)
        .addAction(R.drawable.baseline_thumb_up_24, "High accuracy", thumbUpPendingIntent)
        .addAction(R.drawable.baseline_thumb_down_24, "Low accuracy", thumbDownPendingIntent)
        .setPriority(NotificationCompat.PRIORITY_MAX)
        .build()
}