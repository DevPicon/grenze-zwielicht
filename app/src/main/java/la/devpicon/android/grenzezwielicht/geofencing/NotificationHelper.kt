package la.devpicon.android.grenzezwielicht.geofencing

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import la.devpicon.android.grenzezwielicht.R
import kotlin.random.Random

private const val TAG = "NotificationHelper"
private const val CHANNEL_NAME = "High priority channel"
private const val CHANNEL_ID = "notifications$CHANNEL_NAME"

class NotificationHelper(context: Context) : ContextWrapper(context) {

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannels()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannels() {
        val notificationChannel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            enableLights(true)
            enableVibration(true)
            description = "this is a description for this channel"
            lightColor = Color.RED
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }

    @SuppressLint("MissingPermission")
    fun sendHighPriorityNotification(title: String, body: String, intent: Intent) {
        val pendingIntent = PendingIntent.getActivity(this, 267, intent, FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID).apply {
            setContentTitle(title)
            setContentText(body)
            setSmallIcon(R.drawable.baseline_corporate_fare_24)
            priority = NotificationCompat.PRIORITY_HIGH
            setStyle(NotificationCompat.BigTextStyle().setSummaryText("summary"))
            setContentIntent(pendingIntent)
            setAutoCancel(true)
        }.build()
        NotificationManagerCompat.from(this).notify(Random.nextInt(), notification)

    }

    @SuppressLint("MissingPermission")
    fun createHighPriorityNotificationBuilder(title: String, body: String) = NotificationCompat.Builder(this, CHANNEL_ID).apply {
            setContentTitle(title)
            setContentText(body)
            setSmallIcon(R.drawable.ic_launcher_background)
            setOngoing(true)
        }

}