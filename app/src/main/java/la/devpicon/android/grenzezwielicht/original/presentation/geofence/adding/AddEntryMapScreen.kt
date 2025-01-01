@file:OptIn(ExperimentalMaterial3Api::class)

package la.devpicon.android.grenzezwielicht.original.presentation.geofence.adding

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import la.devpicon.android.grenzezwielicht.latest.ui.theme.GrenzezwielichtTheme

@Composable
fun GeofenceMapScreen(
    initialLatLang: LatLng,
    onCoordinateSelected: (LatLng) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val markerState = rememberMarkerState(position = initialLatLang)

    val cameraPositionState = rememberCameraPositionState{
        position = CameraPosition.fromLatLngZoom(initialLatLang, 15f)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Location") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                onMapLoaded = {},
                cameraPositionState = cameraPositionState,
                onMapClick = { latLng: LatLng -> markerState.position = latLng }
            ) {
                Marker(
                    state = markerState,
                    draggable = true,
                )
            }
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Button(
                    onClick = { onCoordinateSelected(markerState.position) }
                ) {
                    Text("Submit Coordinates")
                }
            }
        }
    }

}

@Preview
@Composable
private fun GeofenceMapScreenPreview() {
    GrenzezwielichtTheme {
        GeofenceMapScreen(
            initialLatLang = LatLng(-33.425748, -70.597016 ),
            onCoordinateSelected = {},
            onCancel = {}
        )
    }
}