package la.devpicon.android.grenzezwielicht.original.presentation.geofence.adding

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.LatLng
import la.devpicon.android.grenzezwielicht.R
import la.devpicon.android.grenzezwielicht.latest.ui.theme.GrenzezwielichtTheme
import la.devpicon.android.grenzezwielicht.original.getCurrentLocation
import la.devpicon.android.grenzezwielicht.original.model.GeofenceEntry
import java.util.UUID

@ExperimentalMaterial3Api
@Composable
fun GeofenceAddEntryScreen(
    geofenceEntryViewModel: GeofenceEntryViewModel = viewModel(),
    popBackStack: () -> Unit,
) {

    val context = LocalContext.current
    var initialLatLng by remember { mutableStateOf<LatLng?>(null) }

    LaunchedEffect(Unit) {
        getCurrentLocation(context) { location ->
            initialLatLng = location?.let { LatLng(it.latitude, it.longitude) }
        }
    }

    if (initialLatLng != null) {
        GeofenceEntryForm(
            popBackStack = popBackStack,
            onSubmit = { geofenceEntry ->
                geofenceEntryViewModel
                    .onSaveGeofenceEntry(geofenceEntry)
                    .also {
                        popBackStack()
                    }
            },
            onCancel = popBackStack,
            screenTitle = "Add geofence",
            geofenceEntry = GeofenceEntry(
                label = "",
                latitude = initialLatLng?.latitude.toString(),
                longitude = initialLatLng?.longitude.toString(),
                radius = 5f
            )
        )
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }

}

@ExperimentalMaterial3Api
@Composable
fun GeofenceEditEntryScreen(
    viewModel: GeofenceEntryViewModel = viewModel(),
    geofenceEntryToEdit: GeofenceEntry,
    popBackStack: () -> Unit,
) {

    GeofenceEntryForm(
        geofenceEntry = geofenceEntryToEdit,
        popBackStack = popBackStack,
        onSubmit = { editedGeofenceEntry ->
            viewModel
                .updateGeofence(editedGeofenceEntry)
                .also {
                    popBackStack()
                }
        },
        onCancel = popBackStack,
        screenTitle = "Edit geofence"
    )

}

@ExperimentalMaterial3Api
@Composable
private fun GeofenceEntryForm(
    geofenceEntry: GeofenceEntry? = null,
    popBackStack: () -> Unit,
    onSubmit: (GeofenceEntry) -> Unit,
    onCancel: () -> Unit,
    screenTitle: String
) {

    var label by remember { mutableStateOf(geofenceEntry?.label.orEmpty()) }
    var latitude by remember { mutableStateOf(geofenceEntry?.latitude ?: "-33.438191") }
    var longitude by remember { mutableStateOf(geofenceEntry?.longitude ?: "-70.65633") }
    var radius by remember { mutableFloatStateOf(geofenceEntry?.radius ?: 5f) }

    var isMapScreenVisible by remember { mutableStateOf(false) }

    if (isMapScreenVisible) {
        GeofenceMapScreen(
            initialLatLang = LatLng(latitude.toDouble(), longitude.toDouble()),
            onCoordinateSelected = { latLng: LatLng ->
                latitude = latLng.latitude.toString()
                longitude = latLng.longitude.toString()
                isMapScreenVisible = false
            },
            onCancel = { isMapScreenVisible = false }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = screenTitle
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = popBackStack,
                            content = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        )
                    }
                )
            }
        ) { paddingValues ->
            EntryForm(
                paddingValues, label, latitude, longitude, radius, onSubmit, geofenceEntry,
                onRadiusValueChange = { value -> radius = value },
                onLongitudeValueChange = { value: String ->
                    longitude = value
                },
                onLatitudeValueChange = { value: String ->
                    latitude = value
                },
                onLabelValueChange = { value: String -> label = value },
                mappVisibleChange = { isMapScreenVisible = true },
                onCancel = onCancel
            )
        }
    }
}

@Composable
private fun EntryForm(
    paddingValues: PaddingValues,
    label: String,
    latitude: String,
    longitude: String,
    radius: Float,
    onSubmit: (GeofenceEntry) -> Unit,
    geofenceEntry: GeofenceEntry?,
    onRadiusValueChange: (Float) -> Unit,
    onLongitudeValueChange: (String) -> Unit,
    onLatitudeValueChange: (String) -> Unit,
    onLabelValueChange: (String) -> Unit,
    mappVisibleChange: () -> Unit,
    onCancel: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
    ) {
        val isLatitudeError = remember { mutableStateOf(false) }
        val isLongitudeError = remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.padding_8)),
                label = { Text(text = "Name:") },
                value = label,
                onValueChange = onLabelValueChange
            )
            CoordinateField(
                title = "Latitude",
                isError = isLatitudeError,
                value = latitude,
                onValueChange = { value: String ->
                    onLatitudeValueChange(value)
                    isLatitudeError.value = value.toFloatOrNull() == null
                },
                errorMessage = "Insert a valid latitude"
            )
            CoordinateField(
                title = "Longitude",
                isError = isLongitudeError,
                value = longitude,
                onValueChange = { value: String ->
                    onLongitudeValueChange(value)
                    isLongitudeError.value = value.toFloatOrNull() == null
                },
                errorMessage = "Insert a valid latitude"
            )
            Button(
                onClick = mappVisibleChange,
                modifier = Modifier.padding(16.dp)
            ) { Text(text = "Set location using map") }
            Spacer(
                modifier = Modifier.padding(
                    vertical = dimensionResource(R.dimen.padding_16),
                    horizontal = dimensionResource(R.dimen.padding_24)
                )
            )

            Text("Geofence Radius:")

            Slider(
                modifier = Modifier.padding(
                    vertical = dimensionResource(R.dimen.padding_16),
                    horizontal = dimensionResource(R.dimen.padding_24)
                ),
                value = radius,
                onValueChange = onRadiusValueChange,
                valueRange = 5f..400f,
                steps = 385

            )
            Text(text = "${radius} m (${radius.toFeet().formatToTwoDecimalPlaces()} ft) / 400 m")
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_16)))
            Buttons(onSubmit, geofenceEntry, label, latitude, longitude, radius, onCancel)
        }
    }
}

@Composable
fun CoordinateField(
    title: String,
    isError: MutableState<Boolean>,
    value: String,
    onValueChange: (String) -> Unit,
    errorMessage: String,
) {
    OutlinedTextField(
        placeholder = {
            Text(text = "0.0")
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.padding_8)),
        label = {
            Text(
                text = title
            )
        },
        supportingText = {
            if (isError.value) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        value = value,
        onValueChange = onValueChange
    )
}

@Composable
private fun Buttons(
    onSubmit: (GeofenceEntry) -> Unit,
    geofenceEntry: GeofenceEntry?,
    label: String,
    latitude: String,
    longitude: String,
    radius: Float,
    onCancel: () -> Unit
) {
    Row {
        Button(
            onClick = {
                onSubmit(
                    GeofenceEntry(
                        id = geofenceEntry?.id ?: UUID.randomUUID().toString(),
                        label = label,
                        latitude = latitude,
                        longitude = longitude,
                        radius = radius
                    )
                )
            }
        ) {
            Text(
                text = "Submit"
            )
        }
        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.padding_8)))
        Button(onClick = onCancel) {
            Text("Cancel")
        }
    }
}

// Helper extension function to format numbers
private fun Float.formatToTwoDecimalPlaces(): String = "%.2f".format(this)

// Conversion function
private fun Float.toFeet(): Float = this * 3.28084f

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun GeofenceAddEntryPreview() {
    GrenzezwielichtTheme {
        GeofenceEntryForm(
            popBackStack = {},
            geofenceEntry = null,
            onSubmit = {},
            onCancel = {},
            screenTitle = ""
        )
    }
}