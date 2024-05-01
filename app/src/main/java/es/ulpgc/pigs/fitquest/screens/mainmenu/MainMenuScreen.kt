package es.ulpgc.pigs.fitquest.screens.mainmenu

import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import es.ulpgc.pigs.fitquest.R
import es.ulpgc.pigs.fitquest.components.ErrorDialog
import es.ulpgc.pigs.fitquest.components.FitquestClickableText
import es.ulpgc.pigs.fitquest.components.FitquestProfilePicture
import es.ulpgc.pigs.fitquest.data.Result
import es.ulpgc.pigs.fitquest.data.User
import es.ulpgc.pigs.fitquest.extensions.fitquestBackground
import es.ulpgc.pigs.fitquest.global.UserGlobalConf
import es.ulpgc.pigs.fitquest.navigation.AppScreens
import es.ulpgc.pigs.fitquest.navigation.BottomNavigationBar
import es.ulpgc.pigs.fitquest.navigation.TopNavigationBar
import es.ulpgc.pigs.fitquest.ui.theme.FitquestTheme

@ExperimentalMaterial3Api
@Composable
fun MainMenuScreen(navController: NavController, backStackEntry: NavBackStackEntry, userGlobalConf: UserGlobalConf) {
    val user by userGlobalConf.currentUser.observeAsState()
    val viewModel: MainMenuViewModel = viewModel(backStackEntry)
    viewModel.setUserGlobalConf(userGlobalConf)
    LaunchedEffect(Unit){
        viewModel.checkIfPictureIsDownloaded()
    }
    val imageState by viewModel.imageState.observeAsState()
    val showDialog = remember { mutableStateOf(false) }
    BackHandler {
        showDialog.value = true
    }
    if (showDialog.value) {
        ConfirmLogoutDialog(showDialog = showDialog, navController)
    }
    Scaffold(
        topBar = { TopNavigationBar(navController = navController, title = stringResource(R.string.topbar_mainmenu_title)) },
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        BodyContent(
            navController = navController,
            user = user,
            imageState = imageState,
            paddingValues = paddingValues
        )
    }
}

@Composable
fun BodyContent(
    navController: NavController,
    user: User?,
    imageState: Result?,
    paddingValues: PaddingValues
) {
    val painter = when (imageState) {
        is Result.ImageSuccess -> rememberAsyncImagePainter(imageState.bytes)
        else -> painterResource(id = R.drawable.default_profile_pic)
    }
    val showDialog = remember { mutableStateOf(false) }
    val errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .fitquestBackground()
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        //FitquestProfilePicture(userProfileImage = painter, onClick = { navController.navigate(route = AppScreens.ProfileScreen.route) } )

        // User name and level
        FitquestClickableText(
            text = user?.getName() ?: "Guest",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            onClick = { navController.navigate(route = AppScreens.ProfileScreen.route) }
        )
        Text(
            text = "Level ${user?.getLevel() ?: "-"}",
            fontSize = 20.sp,
            color = Color.White
        )


        if(showDialog.value){
            ErrorDialog(
                showDialog = showDialog,
                title = "Error",
                message = errorMessage,
                onDismiss = { showDialog.value = false }
            )
        }
    }
}

@Composable
fun ConfirmLogoutDialog(showDialog: MutableState<Boolean>, navController: NavController ) {
    AlertDialog(
        onDismissRequest = {
            showDialog.value = false
        },
        title = { Text("Confirmation") },
        text = { Text("Do you want to log out?") },
        confirmButton = {
            TextButton(
                onClick = {
                    showDialog.value = false
                    navController.navigate(route = AppScreens.LoginScreen.route)
                }
            ) {
                Text("Yes")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    showDialog.value = false
                }
            ) {
                Text("No")
            }
        }
    )
}




@Preview
@ExperimentalMaterial3Api
@Composable
fun ShowPreview() {
    FitquestTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                topBar = { TopNavigationBar(navController = rememberNavController(), title = stringResource(R.string.topbar_mainmenu_title)) },
                bottomBar = { BottomNavigationBar(rememberNavController()) }
            ) { paddingValues ->
                BodyContent(
                    navController = rememberNavController(),
                    user = null,
                    imageState = null,
                    paddingValues = paddingValues
                )
            }
        }
    }
}