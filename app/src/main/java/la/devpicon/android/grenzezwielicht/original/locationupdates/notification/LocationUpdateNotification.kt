package la.devpicon.android.grenzezwielicht.original.locationupdates.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import la.devpicon.android.grenzezwielicht.original.GEOFENCE_POC_CHANNEL_ID
import la.devpicon.android.grenzezwielicht.original.GEOFENCE_POC_CHANNEL_NAME
import la.devpicon.android.grenzezwielicht.R

fun createNotification(context: Context): Notification {

    val channelId = GEOFENCE_POC_CHANNEL_ID
    val channelName = GEOFENCE_POC_CHANNEL_NAME

    val notificationChannel = NotificationChannel(
        channelId,
        channelName,
        NotificationManager.IMPORTANCE_DEFAULT
    ).apply {
        description = "Channel for background location updates"
    }

    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(notificationChannel)

    // Create notification
    return NotificationCompat.Builder(context, channelId)
        .setContentTitle("Location Service active")
        .setContentText("Getting location in background")
        .setSmallIcon(R.drawable.baseline_my_location_24)
        .setOngoing(true)
        .setAutoCancel(false)
        .setPriority(NotificationCompat.PRIORITY_HIGH) // Avoid interruptions or set silence
        .build()
}