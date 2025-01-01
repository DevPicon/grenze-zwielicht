@file:OptIn(ExperimentalLayoutApi::class)

package la.devpicon.android.grenzezwielicht.original.presentation.geofence.event

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.service.notification.StatusBarNotification
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import la.devpicon.android.grenzezwielicht.original.Feedback
import la.devpicon.android.grenzezwielicht.original.GEOFENCE_POC_CHANNEL_ID
import la.devpicon.android.grenzezwielicht.R
import la.devpicon.android.grenzezwielicht.latest.ui.theme.GrenzezwielichtTheme
import la.devpicon.android.grenzezwielicht.original.model.GeofenceEventEntry
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeofenceEventListScreen(
    viewModel: GeofenceEventViewModel,
    popBackStack: () -> Unit,
) {
    val state by viewModel.viewState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Geofence Event List"
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
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.clearEventEntries()
                        clearNotifications(context = context)
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_delete_24),
                            contentDescription = "Clear entries"
                        )
                    }
                }
            )
        },
    ) { paddingValues ->
        Box(modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
        ) {
            when (val currentState = state) {
                GeofenceEventViewModel.LoadingState -> Text(
                    text = "Loading..."
                )
                is GeofenceEventViewModel.ViewState -> {
                    BodySection(currentState.geofenceEventList)
                }
                GeofenceEventViewModel.EmptyState -> Text(
                    text = "There is no geofences"
                )

                else -> {}
            }

        }
    }
}

fun clearNotifications(context: Context) {
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // Retrieve all active notifications
    val activeNotifications: Array<StatusBarNotification> = notificationManager.activeNotifications

    // Iterate through the notifications and cancel those that match the channelId
    activeNotifications.forEach { notification ->
        if (notification.notification.channelId == GEOFENCE_POC_CHANNEL_ID) {
            notificationManager.cancel(notification.id)
        }
    }
    Unit
}

@Composable
private fun BodySection(
    list: List<GeofenceEventEntry>,
    modifier: Modifier = Modifier) {
    var selectedItem by remember { mutableStateOf<GeofenceEventEntry?>(null) }
    var isDialogVisible by remember { mutableStateOf(false) }

    LaunchedEffect(selectedItem) {
        isDialogVisible = selectedItem != null
    }

    LazyColumn {
        items(list) { eventEntry ->
            GeofenceEventItem(
                eventEntry = eventEntry,
                onSelectedItem = { item -> selectedItem = item }
            )
        }
    }

    selectedItem?.let { item ->
        GeofenceEntryDialog(
            selectedItem = item,
            onDismiss = {
                selectedItem = null
                isDialogVisible = false
            },
            modifier = modifier)
    }
}

@Composable
private fun GeofenceEntryDialog(
    selectedItem: GeofenceEventEntry,
    onDismiss: () -> Unit,
    modifier: Modifier
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.large,
            shadowElevation = 8.dp,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = selectedItem.transitionType,
                    modifier = modifier.fillMaxWidth(),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                )
                FlowRow(modifier = modifier.fillMaxWidth()) {
                    Text(text = "Latitude:")
                    Text(text = selectedItem.latitude)
                }
                FlowRow(modifier = modifier.fillMaxWidth()) {
                    Text(text = "Longitude:")
                    Text(text = selectedItem.longitude)
                }
                FlowRow(modifier = modifier.fillMaxWidth()) {
                    Text(text = "Source:")
                    Text(text = selectedItem.geofenceSource, modifier = modifier)
                }
                FlowRow(modifier = modifier.fillMaxWidth()) {
                    Text(text = "Feedback:")
                    Text(text = selectedItem.feedback.name)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Close")
                }
            }
        }
    }
}

@Composable
fun GeofenceEventItem(
    eventEntry: GeofenceEventEntry,
    onSelectedItem: (GeofenceEventEntry) -> Unit,
    modifier: Modifier = Modifier) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                onSelectedItem(eventEntry)
            }
    ) {
        Text(
            text = eventEntry.transitionType,
            modifier = modifier.fillMaxWidth(),
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

        )

        Row(modifier = modifier.fillMaxWidth()) {
            Text(
                text = "Latitude:"
            )
            Text(
                text = eventEntry.latitude
            )
            Spacer(
                modifier = modifier.size(
                    width = 12.dp,
                    height = 8.dp
                )
            )
            Text(
                text = "Longitude:"
            )
            Text(
                text = eventEntry.longitude
            )
        }
        Text(
            text = eventEntry.geofenceSource,
            modifier = modifier.fillMaxWidth()
        )
    }

}

@Preview
@Composable
private fun GeofenceEventItemPreview() {
    GrenzezwielichtTheme {
        Surface {
            GeofenceEventItem(
                eventEntry = GeofenceEventEntry(
                    uuid = UUID.randomUUID().toString(),
                    transitionType = "GEOFENCE_ENTER",
                    latitude = "-70.01324134",
                    longitude = "3.140187f",
                    geofenceSource = "variete",
                    feedback = Feedback.UNDEFINED,
                    brand = Build.BRAND,
                    model = Build.MODEL,
                    osVersion = Build.VERSION.RELEASE,
                    appVersion = ""
                ),
                onSelectedItem = {}
            )

        }
    }
}

@Preview
@Composable
private fun GeofenceTransitionListScreenPreview() {
    GrenzezwielichtTheme {
        BodySection(
            list = listOf(
                GeofenceEventEntry(
                    uuid = UUID.randomUUID().toString(),
                    transitionType = "GEOFENCE_ENTER",
                    latitude = "-70.01324134",
                    longitude = "3.140187f",
                    geofenceSource = "variete",
                    feedback = Feedback.UNDEFINED,
                    brand = Build.BRAND,
                    model = Build.MODEL,
                    osVersion = Build.VERSION.RELEASE,
                    appVersion = ""
                )
            ),
        )
    }
}