package la.devpicon.android.grenzezwielicht.geofencing

import android.annotation.SuppressLint
import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationClient {

    @SuppressLint("MissingPermission")
    fun getLocationUpdates(interval: Long): Flow<Location>

    class LocationException(message: String): Exception()
}
