package la.devpicon.android.grenzezwielicht.original.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_geofence_events")
data class GeofenceEventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val transitionType: String,
    val latitude: Float,
    val longitude: Float,
    val radiusInMeters: Float? = null,
    val radiusInFeet: Double? = null,
    val timestamp: String,
    val deviceManufacturer: String? = null,
    val deviceModel: String? = null,
    val appVersion: String? = null,
    val osVersion: String? = null,
    val geofenceSource:String? = null,
    val isActive:Int = 0,
    val uuid:String,
    val feedback:String

)