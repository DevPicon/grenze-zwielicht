package la.devpicon.android.grenzezwielicht.latest.navigation

enum class Screen(val route:String) {
    Home("home"),
    Permissions("permissions"),
    AddGeofenceEntry("addGeofenceEntry"),
    ViewGeofenceEntryList("viewGeofenceEntries"),
    EditGeofenceEntry("editGeofenceEntry/{geofenceEntry}"),
    LocationServiceScreen("locationServiceScreen"),
    ViewGeofenceEventEntryList("viewGeofenceEventEntryList"),
    ViewStatisticsScreen("viewStatisticsScreen"),
    Maps("map")
}