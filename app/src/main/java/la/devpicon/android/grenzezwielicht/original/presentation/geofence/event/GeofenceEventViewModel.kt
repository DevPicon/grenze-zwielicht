package la.devpicon.android.grenzezwielicht.original.presentation.geofence.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import la.devpicon.android.grenzezwielicht.original.data.repository.GeofenceRepository
import la.devpicon.android.grenzezwielicht.original.model.GeofenceEventEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class GeofenceEventViewModel @Inject constructor(
    private val repository: GeofenceRepository,
) : ViewModel() {

    private val _viewState = MutableStateFlow<GeofenceEventState>(LoadingState)
    val viewState = _viewState.asStateFlow()

    fun loadData() {
        viewModelScope.launch {
            repository.retrieveAllGeofenceEventEntryList()
                .collect { list ->
                    _viewState.update { currentState ->
                        if (list.isEmpty()) {
                            EmptyState
                        } else {
                            ViewState(
                                geofenceEventList = list
                            )
                        }
                    }
                }
        }
    }

    fun clearEventEntries() {
        viewModelScope.launch {
            repository.clearEventEntries()
        }
    }

    sealed interface GeofenceEventState
    data object EmptyState : GeofenceEventState
    data object LoadingState : GeofenceEventState
    data class ViewState(
        val geofenceEventList: List<GeofenceEventEntry> = mutableListOf()
    ) : GeofenceEventState

}