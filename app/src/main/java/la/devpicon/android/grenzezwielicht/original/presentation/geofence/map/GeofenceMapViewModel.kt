package la.devpicon.android.grenzezwielicht.original.presentation.geofence.map

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import la.devpicon.android.grenzezwielicht.original.data.repository.GeofenceRepository
import la.devpicon.android.grenzezwielicht.original.model.GeofenceEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class GeofenceMapViewModel @Inject constructor(
    private val repository: GeofenceRepository
) : ViewModel() {
    private val _viewState = MutableStateFlow<GeofenceMapState>(LoadingState)
    val viewState = _viewState.asStateFlow()

    fun loadData() {
        viewModelScope.launch {
            repository.retrieveAllGeofenceEntryList()
                .collect { list ->
                    _viewState.update { currentState ->
                        when {
                            currentState is GeofenceMapViewState -> currentState.copy(
                                message = "Entries loaded!",
                                geofences = list
                            )
                            else -> GeofenceMapViewState(
                                message = "Entries loaded!",
                                geofences = list
                            )
                        }
                    }
                }
        }
    }

    fun updateUserLocation(currentLocation: Location) {
        _viewState.update { currentState ->
            when (currentState) {
                is GeofenceMapViewState -> {
                    currentState.copy(
                        currentLocation = currentLocation
                    )
                }
                LoadingState -> GeofenceMapViewState(
                    message = "Only user location is updated",
                    currentLocation = currentLocation
                )
            }
        }

    }
}

sealed interface GeofenceMapState
data object LoadingState : GeofenceMapState
data class GeofenceMapViewState(
    val message: String? = null,
    val geofences: List<GeofenceEntry> = emptyList(),
    val currentLocation: Location? = null
) : GeofenceMapState