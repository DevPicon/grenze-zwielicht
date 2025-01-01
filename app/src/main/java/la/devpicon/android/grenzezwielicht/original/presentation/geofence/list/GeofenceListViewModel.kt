package la.devpicon.android.grenzezwielicht.original.presentation.geofence.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import la.devpicon.android.grenzezwielicht.original.data.repository.GeofenceRepository
import la.devpicon.android.grenzezwielicht.original.model.GeofenceEntry
import la.devpicon.android.grenzezwielicht.original.usecase.RegisterGeofenceUC
import la.devpicon.android.grenzezwielicht.original.usecase.UnregisterGeofenceUC
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeofenceListViewModel @Inject constructor(
    private val repository: GeofenceRepository,
    private val registerGeofenceUC: RegisterGeofenceUC,
    private val unregisterGeofenceUC: UnregisterGeofenceUC,
) : ViewModel() {
    private val _viewState = MutableStateFlow<GeofenceListState>(LoadingState)
    val viewState = _viewState.asStateFlow()

    // Cache to store isActive states
    private val isActiveCache = mutableMapOf<String, Boolean>()

    fun loadData() {
        viewModelScope.launch {
            repository.retrieveAllGeofenceEntryList()
                .collect { list ->

                    // Populate the cache with initial isActive states (default to false)
                    list.forEach { geofence ->
                        if (!isActiveCache.containsKey(geofence.id)) {
                            isActiveCache[geofence.id] = false
                        }
                    }

                    _viewState.value = if (list.isEmpty()) {
                        EmptyState
                    } else {
                        GeofenceListViewState(
                            message = "Entries loaded!",
                            geofences = list.map { it.applyCache() }
                        )
                    }
                }
        }
    }

    fun removeGeofenceEntry(entry: GeofenceEntry) {
        viewModelScope.launch {
            isActiveCache.remove(entry.id) // Remove from cache
            repository.removeGeofenceEntry(entry)
            refreshGeofences()
        }
    }

    fun activateGeofence(entry: GeofenceEntry) {
        updateGeofenceState(
            entry = entry,
            isActive = true,
            useCase = { registerGeofenceUC.invoke(it) },
            errorMessage = "Failed to activate geofence with ID: ${entry.id}"
        )
    }

    fun deactivateGeofence(entry: GeofenceEntry) {
        updateGeofenceState(
            entry = entry,
            isActive = false,
            useCase = { unregisterGeofenceUC.invoke(it) },
            errorMessage = "Failed to deactivate geofence with ID: ${entry.id}"
        )
    }

    private fun updateGeofenceState(
        entry: GeofenceEntry,
        isActive: Boolean,
        useCase: suspend (GeofenceEntry) -> Unit,
        errorMessage: String
    ) {
        viewModelScope.launch {
            val geofence = findGeofenceById(entry.id) ?: return@launch

            // Update the cache
            isActiveCache[entry.id] = isActive

            runCatching { useCase(geofence) }
                .onSuccess {
                    refreshGeofences()
                }
                .onFailure { _ ->
                    handleError(errorMessage)
                }
        }
    }

    private fun findGeofenceById(id: String): GeofenceEntry? {
        return (_viewState.value as? GeofenceListViewState)?.geofences?.find { it.id == id }
    }

    private fun refreshGeofences() {
        viewModelScope.launch {
            repository.retrieveAllGeofenceEntryList()
                .collect { list ->
                    _viewState.value = GeofenceListViewState(
                        message = "Entries updated!",
                        geofences = list.map { it.applyCache() }
                    )
                }
        }
    }

    private fun handleError(message: String) {
        updateViewState { state ->
            state.copy(message = message)
        }
    }

    private fun updateViewState(update: (GeofenceListViewState) -> GeofenceListViewState) {
        _viewState.update { currentState ->
            if (currentState is GeofenceListViewState) {
                update(currentState)
            } else {
                currentState
            }
        }
    }

    // Extension function to apply cache values to the geofences
    private fun GeofenceEntry.applyCache(): GeofenceEntry {
        return this.copy(isActive = isActiveCache[this.id] ?: false)
    }

}

sealed interface GeofenceListState
data object EmptyState : GeofenceListState
data object LoadingState : GeofenceListState
data class GeofenceListViewState(
    val message: String? = null,
    val geofences: List<GeofenceEntry> = emptyList(),
) : GeofenceListState


