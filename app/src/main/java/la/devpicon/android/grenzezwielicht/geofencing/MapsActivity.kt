package la.devpicon.android.grenzezwielicht.geofencing

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import la.devpicon.android.grenzezwielicht.R
import java.util.UUID

private const val TAG = "MapsActivity"
private const val GEOFENCE_RADIUS = 200.0
private const val GEOFENCE_ID = "SOME_GEOFENCE_ID"
private val DEFAULT_LOCATION = LatLng(-33.4307342, -70.5949515)
private const val GEOFENCE_TRANSITIONS =
    Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT or Geofence.GEOFENCE_TRANSITION_DWELL

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, LocationCallback {

    private lateinit var mapView: MapView

    private var currentMarker: Marker? = null
    private var mMap: GoogleMap? = null

    private lateinit var geofencingClient: GeofencingClient
    private lateinit var geofenceHelper: GeofenceHelper
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var locationService: LocationService? = null
    private var isServiceBound = false

    private lateinit var pendingIntent: PendingIntent


    private val circleOptions = CircleOptions()
        .center(DEFAULT_LOCATION)
        .radius(GEOFENCE_RADIUS)
        .strokeColor(Color.BLUE)
        .fillColor(Color.argb(70, 0, 0, 255))

    private val REQUIRED_PERMISSIONS = mutableListOf(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    ).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }.toTypedArray()

    @SuppressLint("MissingPermission")
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { isGranted ->
        Log.i(TAG, isGranted.toString())
        if (isGranted.containsValue(false)) {
            Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                requestBackgroundPermission.launch(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            } else {
                getLocation()
            }
        }
    }

    private val requestBackgroundPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                getLocation()
            } else {
                Toast.makeText(this@MapsActivity, "Permission not granted", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as LocationService.LocalBinder
            locationService = binder.getService()
            locationService?.setLocationCallback(this@MapsActivity)
            isServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            locationService = null
            isServiceBound = false
        }

    }

    /**
     * Retrieves the user's current location and starts the location service.
     * If the location is disabled, prompts the user to enable it.
     */
    private fun getLocation() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (isEnabled) {
            val serviceIntent = Intent(this, LocationService::class.java)
                .apply {
                    action = LocationService.ACTION_START
                }
            startService(serviceIntent)
            bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)

            mapView.getMapAsync(this)
        } else {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
            Toast.makeText(this@MapsActivity, "Enable location", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Initializes the activity, sets up the map, and requests necessary permissions.
     *
     * @param savedInstanceState Bundle that contains the saved state of the activity.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        geofencingClient = LocationServices.getGeofencingClient(this)

        mapView = findViewById(R.id.mapview)
        mapView.onCreate(savedInstanceState)

        requestPermissionLauncher.launch(REQUIRED_PERMISSIONS)

        geofenceHelper = GeofenceHelper(this)
        pendingIntent = geofenceHelper.getPendingIntent()
    }

    /**
     * Resumes the map view and the activity lifecycle.
     */
    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    /**
     * Pauses the map view and the activity lifecycle.
     */
    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    /**
     * Destroys the map view and removes geofences when the activity is destroyed.
     */
    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
        geofencingClient.removeGeofences(pendingIntent).run {
            addOnSuccessListener {
                // Geofences removed
                Toast.makeText(
                    this@MapsActivity,
                    "Removed geofence successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
            addOnFailureListener {
                //Failed to remove georeferences
                Toast.makeText(this@MapsActivity, "Removed geofence failed", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    /**
     * Handles low memory situations by notifying the map view.
     */
    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }


    /**
     * Called when the map is ready to be used. Adds geofences and a circle to the map.
     *
     * @param googleMap The GoogleMap object representing the map.
     */
    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        geofencingClient
            .addGeofences(
                geofenceHelper.getGeofencingRequest(
                    geofenceHelper.getGeofence(
                        UUID.randomUUID().toString(),
                        DEFAULT_LOCATION,
                        GEOFENCE_RADIUS.toFloat(),
                        GEOFENCE_TRANSITIONS
                    )
                ), pendingIntent
            )
            .run {
                addOnSuccessListener {
                    mMap?.addCircle(circleOptions)
                    Toast.makeText(
                        this@MapsActivity,
                        "Added geofence successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                addOnFailureListener {
                    Log.i(TAG, "${it.message} ${it.cause} $it")
                    Toast.makeText(
                        this@MapsActivity,
                        "Failed geofence successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    /**
     * Called when the location is updated. Updates the marker on the map with the new location.
     *
     * @param latitude The updated latitude of the user's location.
     * @param longitude The updated longitude of the user's location.
     */
    override fun onLocationUpdated(latitude: Double, longitude: Double) {
        runOnUiThread {
            val myLocation = LatLng(latitude, longitude)
            if (currentMarker != null) {
                currentMarker?.remove()
            }
            currentMarker = mMap?.addMarker(
                MarkerOptions().position(myLocation).title("Marker title")
            )
            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 14.9f))
        }
    }
}