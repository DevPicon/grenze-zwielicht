@file:OptIn(ExperimentalMaterial3Api::class)

package la.devpicon.android.grenzezwielicht.original

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import la.devpicon.android.grenzezwielicht.original.data.broadcast.GeofenceBroadcastReceiver
import la.devpicon.android.grenzezwielicht.original.model.GeofenceEntry
import la.devpicon.android.grenzezwielicht.original.presentation.geofence.adding.GeofenceAddEntryScreen
import la.devpicon.android.grenzezwielicht.original.presentation.geofence.adding.GeofenceEditEntryScreen
import la.devpicon.android.grenzezwielicht.original.presentation.geofence.event.GeofenceEventListScreen
import la.devpicon.android.grenzezwielicht.original.presentation.geofence.event.GeofenceEventViewModel
import la.devpicon.android.grenzezwielicht.original.presentation.geofence.list.GeofenceListScreen
import la.devpicon.android.grenzezwielicht.original.presentation.geofence.list.GeofenceListViewModel
import la.devpicon.android.grenzezwielicht.original.presentation.geofence.map.GeofenceMapViewModel
import la.devpicon.android.grenzezwielicht.original.presentation.geofence.map.MapScreen
import com.vivint.automations.geofence.presentation.home.GeofenceHomeScreen
import com.vivint.automations.geofence.presentation.location.composable.GeofenceLocationServiceScreen
import com.vivint.automations.geofence.presentation.navigation.Screens
import com.vivint.automations.geofence.presentation.permissions.GeofencePermissionScreen
import la.devpicon.android.grenzezwielicht.original.presentation.statistics.GeofenceStatisticsScreen
import la.devpicon.android.grenzezwielicht.original.presentation.statistics.StatisticsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import la.devpicon.android.grenzezwielicht.latest.ui.theme.GrenzezwielichtTheme
import la.devpicon.android.grenzezwielicht.original.presentation.geofence.adding.GeofenceEntryViewModel
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@AndroidEntryPoint
class GeofencePocActivity : ComponentActivity() {

    private var receiver: GeofenceBroadcastReceiver? = null

    @Inject
    internal lateinit var geofenceListViewModel: GeofenceListViewModel

    @Inject
    internal lateinit var geofenceViewModel: GeofenceEntryViewModel

    @Inject
    internal lateinit var statisticsViewModel: StatisticsViewModel

    @Inject
    internal lateinit var geofenceEventViewModel: GeofenceEventViewModel

    @Inject
    internal lateinit var geofenceMapViewModel: GeofenceMapViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            GrenzezwielichtTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .padding(18.dp)
                        .fillMaxSize()
                ) {
                    val navController = rememberNavController()

                    Navigation(navController)

                    val scrollState = rememberScrollState()
                    LaunchedEffect(key1 = Unit) {
                        scrollState.animateScrollTo(100)
                    }
                }
            }
        }
    }

    @Suppress("LongMethod")
    @Composable
    private fun Navigation(navController: NavHostController) {
        NavHost(
            navController = navController,
            startDestination = Screens.Home.route
        ) {
            composable(Screens.Home.route) {
                GeofenceHomeScreen(
                    navigateToPermissions = {
                        navController.navigate(Screens.Permissions.route)
                    },
                    navigateToAddGeofenceEntry = {
                        navController.navigate(Screens.AddGeofenceEntry.route)
                    },
                    navigateToViewGeofenceList = {
                        navController.navigate(Screens.ViewGeofenceEntryList.route)
                    },
                    navigateToLocationServiceScreen = {
                        navController.navigate(Screens.LocationServiceScreen.route)
                    },
                    navigateToViewGeofenceEventList = {
                        navController.navigate(Screens.ViewGeofenceEventEntryList.route)
                    },
                    navigateToViewStatisticsScreen = {
                        navController.navigate(Screens.ViewStatisticsScreen.route)
                    },
                )
            }
            composable(Screens.Permissions.route) {
                GeofencePermissionScreen(
                    popBackStack = {
                        navController.popBackStack()
                    }
                )
            }
            composable(Screens.ViewGeofenceEntryList.route) {
                GeofenceListScreen(
                    viewModel = geofenceListViewModel,
                    popBackStack = {
                        navController.popBackStack()
                    },
                    onEdit = { entry ->
                        navController.navigate(
                            Screens.EditGeofenceEntry
                                .route
                                .replace(
                                    "{geofenceEntry}",
                                    Uri.encode(Json.encodeToString(entry))
                                )
                        )
                    },
                    onShowMap = {
                        navController.navigate(Screens.Maps.route)
                    }
                )
            }
            composable(Screens.EditGeofenceEntry.route) { navBackStackEntry ->
                val entryJson = navBackStackEntry.arguments?.getString("geofenceEntry")
                val geofenceEntry =
                    entryJson?.let { Json.decodeFromString<GeofenceEntry>(it) } ?: return@composable
                GeofenceEditEntryScreen(
                    viewModel = geofenceViewModel,
                    popBackStack = {
                        navController.popBackStack()
                    },
                    geofenceEntryToEdit = geofenceEntry,
                )
            }
            composable(Screens.AddGeofenceEntry.route) {
                GeofenceAddEntryScreen(
                    popBackStack = {
                        navController.popBackStack()
                    },
                    geofenceEntryViewModel = geofenceViewModel
                )
            }
            composable(Screens.LocationServiceScreen.route) {
                GeofenceLocationServiceScreen(
                    popBackStack = {
                        navController.popBackStack()
                    }
                )
            }
            composable(Screens.ViewGeofenceEventEntryList.route) {
                GeofenceEventListScreen(
                    viewModel = geofenceEventViewModel,
                    popBackStack = {
                        navController.popBackStack()
                    }
                )
            }
            composable(Screens.ViewStatisticsScreen.route) {
                GeofenceStatisticsScreen(
                    popBackStack = {
                        navController.popBackStack()
                    },
                    statisticsViewModel = statisticsViewModel,
                    shareStatistics = ::shareStatistics
                )
            }
            composable(Screens.Maps.route) {
                MapScreen(
                    viewModel = geofenceMapViewModel,
                    popBackStack = {
                        navController.popBackStack()
                    },
                )
            }
        }
    }

    fun shareStatistics(csvData: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, csvData)
        }
        startActivity(Intent.createChooser(intent, "Share Statistics"))
    }

    override fun onDestroy() {
        super.onDestroy()
        receiver?.let {
            unregisterReceiver(it)
            receiver = null
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GrenzezwielichtTheme {
        Greeting("Android")
    }
}