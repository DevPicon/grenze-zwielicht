@file:OptIn(ExperimentalMaterial3Api::class)

package la.devpicon.android.grenzezwielicht.original.presentation.geofence.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import la.devpicon.android.grenzezwielicht.R
import la.devpicon.android.grenzezwielicht.latest.ui.theme.GrenzezwielichtTheme
import la.devpicon.android.grenzezwielicht.original.model.GeofenceEntry

@Composable
fun GeofenceListScreen(
    viewModel: GeofenceListViewModel,
    onEdit: (GeofenceEntry) -> Unit,
    popBackStack: () -> Unit,
    onShowMap: () -> Unit
) {

    val state by viewModel.viewState.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadData() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Geofences")
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
                },
                actions = {
                    IconButton(onClick = onShowMap) {
                        Icon(
                            painter = painterResource(R.drawable.sharp_map_24),
                            contentDescription = "Show Geofences on Map"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when (val currentState = state) {
                EmptyState -> Text(
                    text = "There is no geofences"
                )

                is GeofenceListViewState -> GeofenceListBody(
                    geofences = currentState.geofences,
                    onActivate = { entry ->
                        viewModel.activateGeofence(entry)
                    },
                    onEdit = onEdit,
                    onDelete = { entry ->
                        viewModel.removeGeofenceEntry(entry)
                    },
                    onDeactivate = { entry ->
                        viewModel.deactivateGeofence(entry)
                    },
                )

                LoadingState -> Text(
                    text = "Loading..."
                )

            }

        }

    }
}

@Composable
private fun GeofenceListBody(
    geofences: List<GeofenceEntry>,
    onEdit: (GeofenceEntry) -> Unit,
    onActivate: (GeofenceEntry) -> Unit,
    onDelete: (GeofenceEntry) -> Unit,
    onDeactivate: (GeofenceEntry) -> Unit
) {
    LazyColumn {
        items(geofences) { geofence ->
            GeofenceItem(
                geofence = geofence,
                onActivate = onActivate,
                onEdit = onEdit,
                onDelete = onDelete,
                onDeactivate = onDeactivate
            )
        }
    }
}

@Composable
internal fun GeofenceItem(
    geofence: GeofenceEntry,
    onActivate: (GeofenceEntry) -> Unit,
    onEdit: (GeofenceEntry) -> Unit,
    onDelete: (GeofenceEntry) -> Unit,
    onDeactivate: (GeofenceEntry) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(text = "Name: ${geofence.label}")
        Text(text = "Radius: ${geofence.radius} meters")
        Text(text = "Coordinates: ${geofence.getCoordinates()}")

        Spacer(modifier = Modifier.height(8.dp))

        if (geofence.isActive) {
            Button(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = { onDeactivate(geofence) }
            ) {
                Text("Deactivate Geofence")
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(onClick = { onActivate(geofence) }) {
                    Text("Activate")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { onEdit(geofence) }) {
                    Text("Edit")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { onDelete(geofence) }) {
                    Text("Delete")
                }
            }
        }
    }
}

@Preview
@Composable
private fun GeofenceItemPreview() {
    GrenzezwielichtTheme {
        Surface {
            GeofenceItem(
                geofence = GeofenceEntry(
                    label = "Geo 1",
                    latitude = "-33.438191",
                    longitude = "-70.65633"
                ),
                onActivate = {},
                onDeactivate = {},
                onEdit = {},
                onDelete = {}
            )
        }
    }
}

@Preview
@Composable
private fun GeofenceItemActivePreview() {
    GrenzezwielichtTheme {
        Surface {
            GeofenceItem(
                geofence = GeofenceEntry(
                    label = "Geo 1",
                    latitude = "-33.438191",
                    longitude = "-70.65633",
                    isActive = true
                ),
                onActivate = {},
                onDeactivate = {},
                onEdit = {},
                onDelete = {}
            )
        }
    }
}

@Preview
@Composable
private fun GeofenceListScreenPreview() {
    GrenzezwielichtTheme {
        Surface {
            GeofenceListBody(
                geofences = listOf(
                    GeofenceEntry(
                        label = "Geo 1",
                        latitude = "-33.438191",
                        longitude = "-70.65633",
                        isActive = true
                    ),
                    GeofenceEntry(
                        label = "Geo 2",
                        latitude = "-33.438191",
                        longitude = "-70.65633",
                        isActive = false
                    ),
                ),
                onActivate = {},
                onEdit = {},
                onDeactivate = {},
                onDelete = {}
            )
        }
    }
}