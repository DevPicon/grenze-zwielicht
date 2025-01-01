package la.devpicon.android.grenzezwielicht.original.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import la.devpicon.android.grenzezwielicht.original.data.db.GeofenceDatabase
import la.devpicon.android.grenzezwielicht.original.data.db.getGeofenceDatabase
import la.devpicon.android.grenzezwielicht.original.data.location.GeofenceHelper
import la.devpicon.android.grenzezwielicht.original.data.repository.GeofenceRepository
import la.devpicon.android.grenzezwielicht.original.data.repository.GeofenceStatsRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GeofenceModule {

    @Singleton
    @Provides
    fun providesGeofenceStatsRepository(
        db: GeofenceDatabase
    ): GeofenceStatsRepository = GeofenceStatsRepository(db)

    @Singleton
    @Provides
    fun providesGeofenceRepository(
        db: GeofenceDatabase
    ): GeofenceRepository = GeofenceRepository(db, Dispatchers)

    @Singleton
    @Provides
    fun providesGeofenceDatabase(@ApplicationContext context: Context): GeofenceDatabase =
        getGeofenceDatabase(context)

    @Singleton
    @Provides
    fun providesGeofenceHelper(@ApplicationContext context: Context): GeofenceHelper =
        GeofenceHelper(context)

}