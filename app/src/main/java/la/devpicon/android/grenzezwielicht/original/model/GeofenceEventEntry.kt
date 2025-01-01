package la.devpicon.android.grenzezwielicht.original.model

import la.devpicon.android.grenzezwielicht.original.Feedback

data class GeofenceEventEntry(
    val uuid: String,
    val transitionType: String,
    val latitude: String,
    val longitude: String,
    val geofenceSource: String,
    val feedback: Feedback,
    val brand: String,
    val model: String,
    val osVersion: String,
    val appVersion: String,
)