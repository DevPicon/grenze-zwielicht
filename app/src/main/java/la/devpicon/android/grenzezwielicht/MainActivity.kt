package la.devpicon.android.grenzezwielicht

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.location.GeofencingClient
import la.devpicon.android.grenzezwielicht.geofencing.GeofenceHelper
import la.devpicon.android.grenzezwielicht.compose.GeofencingScreen
import la.devpicon.android.grenzezwielicht.ui.theme.GrenzezwielichtTheme

class MainActivity : ComponentActivity() {

    private lateinit var geofenceHelper:GeofenceHelper
    private lateinit var geofencingClient: GeofencingClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            GrenzezwielichtTheme {
               GeofencingScreen()
            }
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