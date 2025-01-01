@file:OptIn(ExperimentalMaterial3Api::class)

package com.vivint.automations.geofence.presentation.location.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vivint.automations.geofence.presentation.location.viewmodel.GeofenceLocationUpdateViewModel

@Composable
fun GeofenceLocationServiceScreen(
    locationViewModel: GeofenceLocationUpdateViewModel = viewModel<GeofenceLocationUpdateViewModel>(),
    modifier: Modifier = Modifier,
    popBackStack: () -> Unit) {
    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Location updates"
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = popBackStack,
                        content = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = ""
                            )
                        }
                    )
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            CommandButtonSection(
                startLocationService = locationViewModel::startLocationService,
                stopLocationService = locationViewModel::stopLocationService
            )
        }
    }
}

@Composable
fun CommandButtonSection(
    startLocationService: () -> Unit,
    stopLocationService: () -> Unit
) {
    Column {
        Button(onClick = startLocationService) {
            Text("Start location service")
        }

        Button(onClick = stopLocationService) {
            Text("Stop location service")
        }
    }
}