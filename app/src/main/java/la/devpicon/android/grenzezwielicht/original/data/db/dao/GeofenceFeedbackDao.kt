package la.devpicon.android.grenzezwielicht.original.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import la.devpicon.android.grenzezwielicht.original.data.db.entity.GeofenceFeedbackEntity

@Dao
interface GeofenceFeedbackDao {

    @Query("INSERT INTO user_geofence_feedback(transitionType,feedback) VALUES (:transitionType,:feedback) ")
    suspend fun insert(transitionType: String, feedback: Boolean): Long

    @Insert
    suspend fun insert(entity: GeofenceFeedbackEntity)

    @Query("SELECT * FROM user_geofence_feedback ORDER BY timestamp ASC")
    fun getAllFeedback(): Flow<List<GeofenceFeedbackEntity>>

}