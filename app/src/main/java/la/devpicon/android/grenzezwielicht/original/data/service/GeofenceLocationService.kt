package la.devpicon.android.grenzezwielicht.original.data.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Looper
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import la.devpicon.android.grenzezwielicht.original.ACTION_LOCATION_UPDATE
import la.devpicon.android.grenzezwielicht.original.EXTRA_LOCATION
import la.devpicon.android.grenzezwielicht.original.LOCATION_FASTEST_INTERVAL
import la.devpicon.android.grenzezwielicht.original.LOCATION_UPDATE_INTERVAL
import la.devpicon.android.grenzezwielicht.original.NOTIFICATION_LOCATION_ID
import la.devpicon.android.grenzezwielicht.original.locationupdates.notification.createNotification

class GeofenceLocationService : Service() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        startForegroundServiceWithNotification()
    }

    // Start service in foreground with a notification
    private fun startForegroundServiceWithNotification() {
        val notification = createNotification(this)
        startForeground(NOTIFICATION_LOCATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "START_LOCATION_UPDATES" -> startLocationUpdates()
            "STOP_LOCATION_UPDATES" -> stopLocationUpdates()
        }
        return START_STICKY
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = LOCATION_UPDATE_INTERVAL
            fastestInterval = LOCATION_FASTEST_INTERVAL
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        // I suppressed the permission granting (ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION)
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    // Location updates callback
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { location ->
                // Store location here or update notification
                // Broadcast location
                val intent = Intent(ACTION_LOCATION_UPDATE).apply {
                    putExtra(EXTRA_LOCATION, location)
                }
                LocalBroadcastManager.getInstance(this@GeofenceLocationService).sendBroadcast(intent)
            }
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    // This service is not designed to be bind (no need bidirectional communication)
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}