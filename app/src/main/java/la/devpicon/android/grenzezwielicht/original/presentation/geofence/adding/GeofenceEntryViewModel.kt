package la.devpicon.android.grenzezwielicht.original.presentation.geofence.adding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import la.devpicon.android.grenzezwielicht.original.data.repository.GeofenceRepository
import la.devpicon.android.grenzezwielicht.original.model.GeofenceEntry
import kotlinx.coroutines.launch
import javax.inject.Inject

class GeofenceEntryViewModel @Inject constructor(
    private val repository: GeofenceRepository,
): ViewModel() {
    fun updateGeofence(editedGeofenceEntry: GeofenceEntry) {
        viewModelScope.launch {
            repository.saveEditedGeofenceEntry(editedGeofenceEntry)
        }
    }

    fun onSaveGeofenceEntry(geofenceEntry: GeofenceEntry) {
        viewModelScope.launch {
            repository.saveGeofenceEntry(geofenceEntry)
        }

    }
}