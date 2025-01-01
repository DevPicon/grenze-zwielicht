package com.vivint.automations.geofence.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import la.devpicon.android.grenzezwielicht.R
import la.devpicon.android.grenzezwielicht.latest.ui.theme.GrenzezwielichtTheme

@Suppress("LongMethod")
@ExperimentalMaterial3Api
@Composable
fun GeofenceHomeScreen(
    navigateToPermissions: () -> Unit,
    navigateToAddGeofenceEntry: () -> Unit,
    navigateToViewGeofenceList: () -> Unit,
    navigateToViewGeofenceEventList: () -> Unit,
    navigateToLocationServiceScreen: () -> Unit,
    navigateToViewStatisticsScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Permissions"
                    )
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = ""
                    )
                }
            )

        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(
                    horizontal = dimensionResource(R.dimen.padding_8),
                    vertical = paddingValues.calculateTopPadding()
                )
        ) {
            CommonListItem(
                title = "Permissions",
                trailingIcon = rememberVectorPainter(Icons.AutoMirrored.Filled.Send),
                onClick = navigateToPermissions,
            )
            CommonListItem(
                onClick = navigateToAddGeofenceEntry,
                title = "Add a geofence",
                trailingIcon = rememberVectorPainter(Icons.AutoMirrored.Filled.Send)
            )
            CommonListItem(
                onClick = navigateToViewGeofenceList,
                title = "View geofences",
                trailingIcon = rememberVectorPainter(Icons.AutoMirrored.Filled.Send)
            )
            CommonListItem(
                onClick = navigateToViewGeofenceEventList,
                title = "View geofence event entries",
                trailingIcon = rememberVectorPainter(Icons.AutoMirrored.Filled.Send)
            )
            CommonListItem(
                onClick = navigateToLocationServiceScreen,
                title = "Location updates",
                trailingIcon = rememberVectorPainter(Icons.AutoMirrored.Filled.Send)
            )
            CommonListItem(
                onClick = navigateToViewStatisticsScreen,
                title = "Statistics",
                trailingIcon = rememberVectorPainter(Icons.AutoMirrored.Filled.Send)
            )
        }
    }
}

@Composable
private fun CommonListItem(
    title: String,
    trailingIcon: VectorPainter,
    onClick: () -> Unit,
) {
    ListItem(
        modifier = Modifier
            .padding(
                vertical = dimensionResource(R.dimen.padding_8)
            )
            .clickable(onClick = onClick),
        headlineContent = {
            Text(
                text = title
            )
        },
        trailingContent = {
            Icon(
                painter = trailingIcon,
                contentDescription = ""
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun GeofenceHomeScreenPreview() {
    GrenzezwielichtTheme {
        GeofenceHomeScreen(
            navigateToPermissions = {},
            navigateToAddGeofenceEntry = {},
            navigateToViewGeofenceList = {},
            navigateToLocationServiceScreen = {},
            navigateToViewGeofenceEventList = {},
            navigateToViewStatisticsScreen = {}
        )
    }
}