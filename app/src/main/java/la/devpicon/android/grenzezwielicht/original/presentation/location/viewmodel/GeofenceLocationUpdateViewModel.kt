package com.vivint.automations.geofence.presentation.location.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import la.devpicon.android.grenzezwielicht.original.ACTION_LOCATION_UPDATE
import la.devpicon.android.grenzezwielicht.original.EXTRA_LOCATION
import la.devpicon.android.grenzezwielicht.original.data.service.GeofenceLocationService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GeofenceLocationUpdateViewModel(application: Application) : AndroidViewModel(application) {

    private val _location = MutableStateFlow<Location?>(null)
    val location = _location.asStateFlow()

    // start location service
    fun startLocationService() {
        val startIntent = Intent(getApplication(), GeofenceLocationService::class.java).apply {
            action = "START_LOCATION_UPDATES"
        }
        getApplication<Application>().startForegroundService(startIntent)
    }

    // stop location service
    fun stopLocationService() {
        val stopIntent = Intent(getApplication(), GeofenceLocationService::class.java).apply {
            action = "STOP_LOCATION_UPDATES"
        }
        getApplication<Application>().stopService(stopIntent)
    }

    fun registerBroadcastReceiver() {
        LocalBroadcastManager.getInstance(getApplication()).registerReceiver(
            locationReceiver,
            IntentFilter(ACTION_LOCATION_UPDATE)
        )
    }

    @SuppressLint("Deprecated")
    private val locationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == ACTION_LOCATION_UPDATE) {
                val userLocation = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(EXTRA_LOCATION, Location::class.java)
                } else {
                    intent.getParcelableExtra<Location>(EXTRA_LOCATION)
                }
                viewModelScope.launch {
                    _location.update {
                        userLocation
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        LocalBroadcastManager.getInstance(getApplication()).unregisterReceiver(locationReceiver)
    }

}