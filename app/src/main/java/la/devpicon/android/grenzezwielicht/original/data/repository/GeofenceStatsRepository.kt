package la.devpicon.android.grenzezwielicht.original.data.repository

import la.devpicon.android.grenzezwielicht.original.data.db.GeofenceDatabase
import javax.inject.Inject

class GeofenceStatsRepository(
    db: GeofenceDatabase
) {
    fun retrieveStats() = geofenceFeedbackDao
        .getAllFeedback()

    private val geofenceFeedbackDao = db.geofenceFeedbackDao()

}