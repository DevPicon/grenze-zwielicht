package la.devpicon.android.grenzezwielicht.original.usecase

import la.devpicon.android.grenzezwielicht.original.data.location.GeofenceHelper
import la.devpicon.android.grenzezwielicht.original.model.GeofenceEntry
import javax.inject.Inject

class UnregisterGeofenceUC @Inject constructor(
    val geofenceHelper: GeofenceHelper
) {
    suspend fun invoke(entry: GeofenceEntry): Result<Boolean> {

        var result: Result<Boolean> = Result.failure(Throwable(""))
        geofenceHelper.deregisterGeofence()
            .onSuccess {
                geofenceHelper.removeGeofence(entry.id)?.let {
                    result = Result.success(true)
                }
            }
            .onFailure { e ->
                result = Result.failure(e)
            }
        return result
    }
}