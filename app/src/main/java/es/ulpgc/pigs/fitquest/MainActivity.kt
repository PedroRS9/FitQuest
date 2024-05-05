package es.ulpgc.pigs.fitquest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import es.ulpgc.pigs.fitquest.navigation.AppNavigation
import es.ulpgc.pigs.fitquest.ui.theme.FitquestTheme

@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FitquestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                    Content()
                }
            }
        }
    }
    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun Content() {
        val activityPermissionState = rememberPermissionState(permission = android.Manifest.permission.ACTIVITY_RECOGNITION)

        Column {
            Button(onClick = {

            }) {
                // Don't delete de Text since development. It's a permission log
                Text(text = "Activity Permission ${activityPermissionState.status.isGranted}")
                LaunchedEffect(Unit){
                    activityPermissionState.launchPermissionRequest()
                }
            }
        }
    }
}






@Preview(showBackground = true)
@ExperimentalMaterial3Api
@Composable
fun DefaultPreview(){
    FitquestTheme{
        AppNavigation()
    }
}