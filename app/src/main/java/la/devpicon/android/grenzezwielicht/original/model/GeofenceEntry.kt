package la.devpicon.android.grenzezwielicht.original.model

import kotlinx.serialization.Serializable

@Serializable
data class GeofenceEntry(
    val id: String = "",
    val label: String = "",
    val latitude: String = "0.0",
    val longitude: String = "0.0",
    val radius: Float = 5f,
    val isActive: Boolean = false
) {
    fun getCoordinates() = "$latitude lat, $longitude lon"
}