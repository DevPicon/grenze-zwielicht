package la.devpicon.android.grenzezwielicht

import android.app.Application
import android.os.Build
import la.devpicon.android.grenzezwielicht.geofencing.NotificationHelper

class LocationApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationHelper(this)
        }
    }
}
