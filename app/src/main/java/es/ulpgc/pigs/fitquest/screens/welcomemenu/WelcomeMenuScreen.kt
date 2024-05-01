package es.ulpgc.pigs.fitquest.screens.welcomemenu

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import es.ulpgc.pigs.fitquest.R
import es.ulpgc.pigs.fitquest.components.FitquestSocialMediaIcons
import es.ulpgc.pigs.fitquest.components.FitquestTransparentButton
import es.ulpgc.pigs.fitquest.extensions.fitquestBackground
import es.ulpgc.pigs.fitquest.extensions.fitquestLoginBackground
import es.ulpgc.pigs.fitquest.navigation.AppScreens
import es.ulpgc.pigs.fitquest.ui.theme.FitquestTheme

@Composable
fun WelcomeScreen(navController: NavController) {
        val activity = LocalContext.current as Activity
        Column {
                BodyContent(navController = navController)
        }
        BackHandler {
                activity.finishAffinity()
        }
}

@Composable
fun BodyContent(navController: NavController) {
        Column(
                modifier = Modifier.fitquestLoginBackground(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
        ) {

                Image(
                        painter = painterResource(R.drawable.fitquest_logo),
                        contentDescription = stringResource(R.string.fitquest_logo_description),
                        modifier = Modifier
                                .size(200.dp)
                )


                Text(
                        text = stringResource(R.string.welcome_message),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.padding(16.dp),
                        color = Color.White
                )

                
                FitquestTransparentButton(
                        onClick = { navController.navigate(route = AppScreens.LoginScreen.route) },
                        text = stringResource(R.string.button_signin),
                        modifier = Modifier.padding(20.dp),
                )

                FitquestTransparentButton(
                        onClick = { navController.navigate(route = AppScreens.SignupScreen.route) },
                        text = stringResource(R.string.button_signup),
                        modifier = Modifier.padding(20.dp),
                )

                FitquestSocialMediaIcons()
                
        }
}

@Preview(showBackground = true)
@Composable
fun ShowPreview() {
        FitquestTheme {
                Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                ) {
                        BodyContent(
                                navController = rememberNavController()
                        )
                }
        }
}


