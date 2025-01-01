package com.vivint.automations.geofence.presentation.permissions

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import la.devpicon.android.grenzezwielicht.latest.ui.theme.GrenzezwielichtTheme

@ExperimentalMaterial3Api
@Composable
fun GeofencePermissionScreen(
    popBackStack: () -> Unit,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Permissions"
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
            LocationPermissionSection(context)
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun GeofencePermissionScreenPreview() {
    GrenzezwielichtTheme {
        GeofencePermissionScreen(
            popBackStack = {}
        )
    }
}