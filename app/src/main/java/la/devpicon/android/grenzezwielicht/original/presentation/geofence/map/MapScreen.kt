package la.devpicon.android.grenzezwielicht.original.presentation.geofence.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.vivint.automations.geofence.presentation.location.viewmodel.GeofenceLocationUpdateViewModel

@ExperimentalMaterial3Api
@Composable
fun MapScreen(
    viewModel: GeofenceMapViewModel,
    locationViewModel: GeofenceLocationUpdateViewModel = viewModel<GeofenceLocationUpdateViewModel>(),
    popBackStack: () -> Unit
) {
    val state by viewModel.viewState.collectAsState()
    val location by locationViewModel.location.collectAsState()
    val cameraPositionState = rememberCameraPositionState()
    var cameraMoved by remember { mutableStateOf(false) }
    var showNoLocationSnackbar by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadData()
        locationViewModel.registerBroadcastReceiver()
    }

    LaunchedEffect(location) {
        showNoLocationSnackbar = location == null
        if (!cameraMoved && location != null) {
            cameraMoved = true
            location?.let {
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(it.latitude, it.longitude),
                        15f
                    ),
                    1000
                )
            }
        }
        location?.let { viewModel.updateUserLocation(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Geofences Map") },
                navigationIcon = {
                    IconButton(onClick = popBackStack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        snackbarHost = {
            if (showNoLocationSnackbar) {
                Snackbar(
                    modifier = Modifier.padding(8.dp),
                    action = {
                        TextButton(onClick = { showNoLocationSnackbar = false }) {
                            Text("Dismiss")
                        }
                    }
                ) {
                    Text("No location updates are being received")
                }
            }
        }
    ) { paddingValues ->
        MapContent(paddingValues, state, cameraPositionState)
    }
}

@Composable
private fun MapContent(
    paddingValues: PaddingValues,
    state: GeofenceMapState,
    cameraPositionState: CameraPositionState
) {
    Box(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
    ) {
        when (val currentState = state) {
            is GeofenceMapViewState -> {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState
                ) {
                    currentState.geofences.forEach { geofence ->
                        val circleColor = if (geofence.isActive) Color.Red else Color.Gray
                        Circle(
                            center = LatLng(geofence.latitude.toDouble(), geofence.longitude.toDouble()),
                            radius = geofence.radius.toDouble(),
                            strokeColor = circleColor,
                            fillColor = circleColor.copy(alpha = 0.3f),
                            strokeWidth = 2f
                        )
                    }

                    currentState.currentLocation?.let {
                        Marker(
                            state = MarkerState(position = LatLng(it.latitude, it.longitude)),
                            title = "You are here",
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                        )
                    }
                }
            }
            LoadingState -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

    }
}
