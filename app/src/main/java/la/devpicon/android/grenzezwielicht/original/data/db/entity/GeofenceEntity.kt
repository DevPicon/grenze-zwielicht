package la.devpicon.android.grenzezwielicht.original.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_geofences")
data class GeofenceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val label: String,
    val latitude: Float,
    val longitude: Float,
    val radius: Float,
    val isActive: Boolean = false
)