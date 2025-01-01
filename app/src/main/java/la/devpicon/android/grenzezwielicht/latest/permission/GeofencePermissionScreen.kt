package la.devpicon.android.grenzezwielicht.latest.permission

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState

/**
 * According to Android Documentation the following options require a permission:
 *
 *  - Geofence monitoring requires ACCESS_FINE_LOCATION and ACCESS_BACKGROUND_LOCATION
 *
 * See [Create and monitor geofences](https://developer.android.com/develop/sensors-and-location/location/geofencing)
 */

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun GeofencePermissionScreen(
    permissionManager: PermissionManager,
    modifier: Modifier = Modifier
) {
    val uniquePermissions = permissionManager.getUniquePermissions().map { it.permission }
    val permissionsState = rememberMultiplePermissionsState(uniquePermissions.toList())

    LaunchedEffect(permissionsState.allPermissionsGranted) {
        val grantedPermissions = permissionsState.permissions
            .filter { it.status.isGranted }
            .map { it.permission }
            .toSet()

        permissionManager.checkFeatureCallbacks(grantedPermissions)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Required permissions",
            style = MaterialTheme.typography.headlineSmall
        )

        permissionManager.getUniquePermissions().forEach { permission ->
            val isGranted = permissionsState.permissions.find {
                it.permission == permission.permission
            }?.status?.isGranted ?: false

            Button(
                onClick = {
                    permissionsState.permissions.find {
                        it.permission == permission.permission
                    }?.launchPermissionRequest()
                },
                enabled = !isGranted,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = when {
                        isGranted -> "✓ ${permission.displayName}"
                        else -> "Grant ${permission.displayName}"
                    }
                )
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Show features and their status
        Text(
            text = "Features",
            style = MaterialTheme.typography.headlineSmall
        )

        FeatureStatusList(
            permissionManager = permissionManager,
            permissionsState = remember(permissionsState) {
                derivedStateOf {
                    permissionsState.permissions
                        .filter { it.status.isGranted }
                        .map { it.permission }
                        .toSet()
                }
            }
        )

    }
}

@Composable
private fun FeatureStatusList(
    permissionManager: PermissionManager,
    permissionsState: androidx.compose.runtime.State<Set<String>>
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        permissionManager.getFeatures().forEach { feature ->
            FeatureStatusItem(
                feature = feature,
                grantedPermissions = permissionsState.value
            )
        }
    }
}

@Composable
internal fun FeatureStatusItem(
    feature: Feature,
    grantedPermissions: Set<String>,
    modifier: Modifier = Modifier
) {
    val isEnabled = feature.requiredPermissions.all { it in grantedPermissions }
    val missingPermissions = feature.requiredPermissions - grantedPermissions

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = feature.name,
                    style = MaterialTheme.typography.titleMedium
                )

                Icon(
                    imageVector = if (isEnabled) {
                        Icons.Default.CheckCircle
                    } else {
                        Icons.Default.Warning
                    },
                    contentDescription = if (isEnabled) {
                        "Feature enabled"
                    } else {
                        "Missing permissions"
                    },
                    tint = if (isEnabled) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                )
            }

            if (!isEnabled) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Missing permissions: ${
                        missingPermissions.joinToString {
                            PermissionManager.getPermissionDisplayName(it)
                        }
                    }",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}