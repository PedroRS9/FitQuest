package es.ulpgc.pigs.fitquest

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import es.ulpgc.pigs.fitquest.global.PERMISSION_REQUEST_CODE
import es.ulpgc.pigs.fitquest.global.RequestPermissionButton

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
                }
            }
        }
    }
/*
    Esto es por el tema de los permisos
    Por ahora te los pide si no los tienes
    

    setContent {
        FitquestTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Otros componentes de tu UI

                    RequestPermissionButton(
                        permission = android.Manifest.permission.ACTIVITY_RECOGNITION,
                        onPermissionGranted = {
                            // Permiso concedido, realizar acciones necesarias
                        },
                        onPermissionDenied = {
                            // Permiso denegado, mostrar mensaje o realizar acciones necesarias
                        }
                    )
                }
            }
        }
    }

 */


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    // Permiso concedido
                    // Realizar acciones necesarias, como iniciar el contador de pasos
                } else {
                    // Permiso denegado
                    // Realizar acciones necesarias, como mostrar un mensaje de que se necesita el permiso
                }
                return
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