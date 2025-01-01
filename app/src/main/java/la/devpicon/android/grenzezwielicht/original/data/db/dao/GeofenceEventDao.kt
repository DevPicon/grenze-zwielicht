package la.devpicon.android.grenzezwielicht.original.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import la.devpicon.android.grenzezwielicht.original.Feedback
import la.devpicon.android.grenzezwielicht.original.data.db.entity.GeofenceEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GeofenceEventDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(geofenceEventEntity: GeofenceEventEntity)

    @Query("SELECT * FROM user_geofence_events ORDER BY timestamp ASC")
    fun getAllGeofenceEvents(): Flow<List<GeofenceEventEntity>>

    @Query("UPDATE user_geofence_events SET feedback = :feedback WHERE uuid = :geofenceTransitionUuid")
    fun update(geofenceTransitionUuid: String, feedback: Feedback)

    @Query("DELETE FROM user_geofence_events")
    suspend fun deleteAllEventEntries()
}