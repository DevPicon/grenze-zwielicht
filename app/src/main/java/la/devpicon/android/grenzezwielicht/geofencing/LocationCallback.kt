package la.devpicon.android.grenzezwielicht.geofencing

interface LocationCallback {
    fun onLocationUpdated(latitude: Double, longitude: Double)
}