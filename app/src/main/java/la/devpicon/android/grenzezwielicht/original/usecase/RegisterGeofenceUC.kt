package la.devpicon.android.grenzezwielicht.original.usecase

import android.location.Location
import la.devpicon.android.grenzezwielicht.original.data.location.GeofenceHelper
import la.devpicon.android.grenzezwielicht.original.model.GeofenceEntry
import javax.inject.Inject

class RegisterGeofenceUC @Inject constructor(
    val geofenceHelper: GeofenceHelper
) {
    fun invoke(entry: GeofenceEntry):Result<Boolean>{
        // Coordinates must be wrap with a Location objectrevolr
        val location = Location("")
            .apply {
                latitude = entry.latitude.toDouble()
                longitude = entry.longitude.toDouble()
            }

        geofenceHelper.addGeofence(
            key = entry.id,
            location = location,
            radiusInMeters = entry.radius
        )

        var result:Result<Boolean> = Result.failure(Throwable(""))
        geofenceHelper.registerGeofence(
            onSuccess = {
                result = Result.success(true)
            },
            onFailure = {
                result = Result.failure(Throwable(""))
            }
        )
        return result
    }
}