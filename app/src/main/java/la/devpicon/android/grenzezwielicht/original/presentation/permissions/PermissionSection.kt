package com.vivint.automations.geofence.presentation.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.core.app.ActivityCompat

private val REQUIRED_PERMISSIONS = mutableListOf(
    Manifest.permission.ACCESS_FINE_LOCATION
).toTypedArray()

private val REQUIRED_COARSE_PERMISSIONS = mutableListOf(
    Manifest.permission.ACCESS_COARSE_LOCATION
).toTypedArray()

private val REQUIRED_ACCESS_BACKGROUND_LOCATION_PERMISSION = mutableListOf(
    Manifest.permission.ACCESS_BACKGROUND_LOCATION
).toTypedArray()

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
private val REQUIRED_FOREGROUND_SERVICE_LOCATION_PERMISSION = mutableListOf(
    Manifest.permission.FOREGROUND_SERVICE_LOCATION
).toTypedArray()

@Suppress("LongMethod")
@Composable
internal fun LocationPermissionSection(
    context: Context,
) {
    var permissionPreciseGranted by remember {
        mutableStateOf(
            when {
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED -> true
                else -> false
            }
        )
    }

    var permissionCoarseGranted by remember {
        mutableStateOf(
            when {
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED -> true
                else -> false
            }
        )
    }

    var permissionBackgroundLocationGranted by remember {
        mutableStateOf(
            when {
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) == PackageManager.PERMISSION_GRANTED -> true
                else -> false
            }
        )
    }

    var permissionForegroundServiceLocationGranted by remember {
        mutableStateOf(
            when {
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.FOREGROUND_SERVICE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED -> true
                else -> false
            }
        )
    }

    val requestPermissionLauncher = requestPrecisePermission(
        onGranted = { permissionPreciseGranted = true },
        onDenied = { permissionPreciseGranted = false }
    )

    val coarseLocationLauncher = requestCoarsePermission(
        onGranted = { permissionCoarseGranted = true },
        onDenied = { permissionCoarseGranted = false }
    )

    val backgroundLocation = requestBackgroundLocationPermission(
        onGranted = { permissionBackgroundLocationGranted = true },
        onDenied = { permissionBackgroundLocationGranted = false }
    )

    val foregroundServiceLocation = requestForegroundServiceLocationPermission(
        onGranted = { permissionForegroundServiceLocationGranted = true },
        onDenied = { permissionBackgroundLocationGranted = false }
    )

    Content(requestPermissionLauncher,
        permissionPreciseGranted,
        coarseLocationLauncher,
        permissionCoarseGranted,
        backgroundLocation,
        permissionBackgroundLocationGranted,
        foregroundServiceLocation
    )
}

@Composable
private fun Content(
    requestPermissionLauncher: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>,
    permissionPreciseGranted: Boolean,
    coarseLocationLauncher: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>,
    permissionCoarseGranted: Boolean,
    backgroundLocation: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>,
    permissionBackgroundLocationGranted: Boolean,
    foregroundServiceLocation: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {

        Button(onClick = { requestPermissionLauncher.launch(REQUIRED_PERMISSIONS) }) {
            Text(text = "Request PRECISE LOCATION Permissions : ${if (permissionPreciseGranted) "Granted" else "Denied"}")
        }

        Button(onClick = { coarseLocationLauncher.launch(REQUIRED_COARSE_PERMISSIONS) }) {
            Text(text = "Request COARSE LOCATION Permissions: ${if (permissionCoarseGranted) "Granted" else "Denied"}")
        }

        Text("ACCESS_BACKGROUND_LOCATION is required to Getting Location Update and Retrieving geofence transitions")
        Button(onClick = { backgroundLocation.launch(REQUIRED_ACCESS_BACKGROUND_LOCATION_PERMISSION) }) {
            Text(text = "Request ACCESS BACKGROUND LOCATION Permission: ${if (permissionBackgroundLocationGranted) "Granted" else "Denied"}")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            Text("FOREGROUND_SERVICE_LOCATION is required to Getting Location Update")
            Button(onClick = {
                foregroundServiceLocation.launch(REQUIRED_FOREGROUND_SERVICE_LOCATION_PERMISSION)
            }
            ) {
                Text(text = "Request FOREGROUND SERVICE LOCATION Permission: ${if (permissionBackgroundLocationGranted) "Granted" else "Denied"}")
            }
        }

    }
}

@Composable
private fun requestForegroundServiceLocationPermission(
    onGranted: () -> Unit,
    onDenied: () -> Unit
): ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>> {
    return rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.FOREGROUND_SERVICE_LOCATION, false) -> {
                onGranted()
            }
            else -> {
                // Declined
                onDenied()
            }
        }
    }
}

@Composable
private fun requestCoarsePermission(
    onGranted: () -> Unit,
    onDenied: () -> Unit
): ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>> {
    return rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                onGranted()
            }
            else -> {
                // Declined
                onDenied()
            }
        }
    }
}

@Composable
private fun requestBackgroundLocationPermission(
    onGranted: () -> Unit,
    onDenied: () -> Unit
): ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>> {
    return rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_BACKGROUND_LOCATION, false) -> {
                onGranted()
            }
            else -> {
                // Declined
                onDenied()
            }
        }
    }
}

@Composable
private fun requestPrecisePermission(
    onGranted: () -> Unit,
    onDenied: () -> Unit
): ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>> {
    return rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location granted
                onGranted()
            }
            else -> {
                // Declined
                onDenied()
            }
        }
    }
}