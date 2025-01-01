package la.devpicon.android.grenzezwielicht.original.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import la.devpicon.android.grenzezwielicht.original.data.db.entity.GeofenceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GeofenceDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(geofenceEntity: GeofenceEntity): Long

    @Update
    suspend fun update(geofenceEntity: GeofenceEntity)

    @Delete
    suspend fun delete(geofenceEntity: GeofenceEntity)

    @Query("SELECT * FROM user_geofences ORDER BY label ASC")
    fun getAllGeofences(): Flow<List<GeofenceEntity>>

    @Query("DELETE FROM user_geofences")
    suspend fun deleteAllGeofences()

    @Query("UPDATE user_geofences SET isActive = :active WHERE id = :geofenceId")
    fun updateGeofence(geofenceId: Int, active: Boolean)

    @Query("SELECT * FROM user_geofences WHERE id = :requestId")
    suspend fun getGeofence(requestId: Int): GeofenceEntity?
}