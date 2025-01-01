package la.devpicon.android.grenzezwielicht.latest.permission

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun FeatureStatusItemPreview() {
    FeatureStatusItem(
        feature = Feature(
            name = "Geofencing",
            requiredPermissions = setOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ),
            onAllPermissionsGranted = {}
        ),
        grantedPermissions = emptySet(),
        modifier = Modifier
    )
}