package la.devpicon.android.grenzezwielicht.latest.permission

import android.Manifest

class PermissionManager {
    private val allPermissions = mutableSetOf<Permission>()
    private val features = mutableListOf<Feature>()

    fun getFeatures(): List<Feature> = features.toList()

    fun addFeature(feature: Feature) {
        features.add(feature)
        feature.requiredPermissions.forEach { requiredPermission ->
            allPermissions.add(
                Permission(
                    permission = requiredPermission,
                    displayName = getPermissionDisplayName(requiredPermission)
                )
            )
        }
    }

    fun getUniquePermissions(): Set<Permission> = allPermissions

    fun checkFeatureCallbacks(grantedPermissions: Set<String>) {
        features.forEach { feature ->
            if (feature.requiredPermissions.all { it in grantedPermissions }) {
                feature.onAllPermissionsGranted()
            }
        }
    }

    companion object {
        fun getPermissionDisplayName(permission: String): String = when (permission) {
            Manifest.permission.ACCESS_FINE_LOCATION -> "Precise Location"
            Manifest.permission.ACCESS_BACKGROUND_LOCATION -> "Background Location"
            Manifest.permission.CAMERA -> "Camera"
            Manifest.permission.RECORD_AUDIO -> "Microphone"
            else -> permission.split(".").last()
        }
    }
}


data class Permission(
    val permission: String,
    val displayName: String
)

data class Feature(
    val name: String,
    val requiredPermissions: Set<String>,
    val onAllPermissionsGranted: () -> Unit
)