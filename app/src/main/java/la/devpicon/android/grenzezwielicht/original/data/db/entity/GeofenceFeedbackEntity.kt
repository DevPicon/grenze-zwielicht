package la.devpicon.android.grenzezwielicht.original.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_geofence_feedback")
data class GeofenceFeedbackEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val transitionType: String,
    val feedback: Boolean,
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val timestamp: String
)