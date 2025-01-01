package com.vivint.automations.geofence.presentation.navigation

enum class Screens(val route: String) {
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