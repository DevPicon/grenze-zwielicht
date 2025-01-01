package la.devpicon.android.grenzezwielicht.original.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import la.devpicon.android.grenzezwielicht.original.data.db.dao.GeofenceDao
import la.devpicon.android.grenzezwielicht.original.data.db.dao.GeofenceEventDao
import la.devpicon.android.grenzezwielicht.original.data.db.dao.GeofenceFeedbackDao
import la.devpicon.android.grenzezwielicht.original.data.db.entity.GeofenceEntity
import la.devpicon.android.grenzezwielicht.original.data.db.entity.GeofenceEventEntity
import la.devpicon.android.grenzezwielicht.original.data.db.entity.GeofenceFeedbackEntity

@Database(entities = [GeofenceEntity::class, GeofenceEventEntity::class, GeofenceFeedbackEntity::class], version = 4)
abstract class GeofenceDatabase : RoomDatabase() {
    abstract fun geofenceDao(): GeofenceDao
    abstract fun geofenceEventDao(): GeofenceEventDao
    abstract fun geofenceFeedbackDao(): GeofenceFeedbackDao
}

/**
 * @param context [Context]
 * @return [GeofenceDatabase]
 */
fun getGeofenceDatabase(context: Context): GeofenceDatabase {
    return Room.databaseBuilder(
        context,
        GeofenceDatabase::class.java,
        GEOFENCE_DB,
    )
        .fallbackToDestructiveMigration()
        .build()
}

private const val GEOFENCE_DB = "geofence-events-db"
