package la.devpicon.android.grenzezwielicht.original.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import la.devpicon.android.grenzezwielicht.original.Feedback
import la.devpicon.android.grenzezwielicht.original.data.db.GeofenceDatabase
import la.devpicon.android.grenzezwielicht.original.data.db.entity.GeofenceEntity
import la.devpicon.android.grenzezwielicht.original.data.db.entity.GeofenceEventEntity
import la.devpicon.android.grenzezwielicht.original.getTransitionType
import la.devpicon.android.grenzezwielicht.original.model.GeofenceEntry
import la.devpicon.android.grenzezwielicht.original.model.GeofenceEventEntry

class GeofenceRepository(
    db: GeofenceDatabase,
    val dispatchers: Dispatchers = Dispatchers
) {
    suspend fun saveGeofenceEntry(geofenceEntry: GeofenceEntry): Long {
        return geofenceDao.insert(
            GeofenceEntity(
                label = geofenceEntry.label,
                latitude = geofenceEntry.latitude.toFloat(),
                longitude = geofenceEntry.longitude.toFloat(),
                radius = geofenceEntry.radius
            )
        )
    }

    suspend fun saveEditedGeofenceEntry(geofenceEntry: GeofenceEntry) {
        geofenceDao.update(
            GeofenceEntity(
                id = geofenceEntry.id.toInt(),
                label = geofenceEntry.label,
                latitude = geofenceEntry.latitude.toFloat(),
                longitude = geofenceEntry.longitude.toFloat(),
                radius = geofenceEntry.radius
            )
        )
    }

    suspend fun removeGeofenceEntry(geofenceEntry: GeofenceEntry) {
        geofenceDao.delete(
            GeofenceEntity(
                id = geofenceEntry.id.toInt(),
                label = geofenceEntry.label,
                latitude = geofenceEntry.latitude.toFloat(),
                longitude = geofenceEntry.longitude.toFloat(),
                radius = geofenceEntry.radius
            )
        )
    }

    fun retrieveAllGeofenceEntryList() = geofenceDao.getAllGeofences().map { list ->
        list.map {
            GeofenceEntry(
                id = it.id.toString(),
                label = it.label,
                latitude = it.latitude.toString(),
                longitude = it.longitude.toString(),
                radius = it.radius,
                isActive = it.isActive
            )
        }
    }

    fun retrieveAllGeofenceEventEntryList() = geofenceEventDao.getAllGeofenceEvents().map { list ->
        list.map {
            GeofenceEventEntry(
                uuid = it.uuid,
                transitionType = it.transitionType,
                latitude = it.latitude.toString(),
                longitude = it.longitude.toString(),
                geofenceSource = it.geofenceSource.orEmpty(),
                feedback = Feedback.valueOf(it.feedback),
                brand = it.deviceManufacturer.orEmpty(),
                model = it.deviceModel.orEmpty(),
                osVersion = it.osVersion.orEmpty(),
                appVersion = it.appVersion.orEmpty(),
                timestamp = it.timestamp.toLong()
            )
        }
    }

    suspend fun saveGeofenceEventEntry(geofenceEventEntry: GeofenceEventEntry) {
        with(geofenceEventEntry) {
            geofenceEventDao.insert(
                GeofenceEventEntity(
                    transitionType = transitionType,
                    latitude = latitude.toFloat(),
                    longitude = longitude.toFloat(),
                    geofenceSource = geofenceSource,
                    timestamp = "$timestamp",
                    uuid = uuid,
                    feedback = feedback.name,
                    deviceManufacturer = brand,
                    deviceModel = model,
                    osVersion = osVersion,
                    appVersion = appVersion
                )
            )
        }
    }

    suspend fun insertFeedback(
        isAccurate: Boolean,
        transitionType: Int
    ) {
        geofenceFeedbackDao.insert(getTransitionType(transitionType), isAccurate)
    }

    fun updateTransitionFeedback(geofenceTransitionUuid: String, feedback: Feedback) {
        geofenceEventDao.update(geofenceTransitionUuid, feedback)
    }

    suspend fun clearEventEntries() = withContext(dispatchers.IO) {
        geofenceEventDao.deleteAllEventEntries()
    }

    suspend fun getGeofence(requestId: String): GeofenceEntity? = withContext(dispatchers.IO) {
        geofenceDao.getGeofence(requestId.toInt())
    }


    private val geofenceDao = db.geofenceDao()
    private val geofenceEventDao = db.geofenceEventDao()
    private val geofenceFeedbackDao = db.geofenceFeedbackDao()

}
