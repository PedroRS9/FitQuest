package es.ulpgc.pigs.fitquest.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import es.ulpgc.pigs.fitquest.global.UserGlobalConf
import es.ulpgc.pigs.fitquest.screens.loginscreen.LoginScreen
import es.ulpgc.pigs.fitquest.screens.mainmenu.MainMenuScreen
import es.ulpgc.pigs.fitquest.screens.profile.ProfileScreen
import es.ulpgc.pigs.fitquest.screens.signup.SignupScreen
import es.ulpgc.pigs.fitquest.screens.welcomemenu.WelcomeScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val userGlobalConf = UserGlobalConf()
    NavHost(navController = navController, startDestination = AppScreens.WelcomeScreen.route){
        composable(route = AppScreens.WelcomeScreen.route){
            WelcomeScreen(navController)
        }
        composable(route = AppScreens.LoginScreen.route){ backStackEntry ->
            LoginScreen(navController, backStackEntry, userGlobalConf)
        }
        composable(route = AppScreens.SignupScreen.route){ backStackEntry ->
            SignupScreen(navController, backStackEntry)
        }
        composable(route = AppScreens.MainMenuScreen.route){ backStackEntry ->
            MainMenuScreen(navController, backStackEntry, userGlobalConf)
        }
        composable(route = AppScreens.ProfileScreen.route){ backStackEntry ->
            ProfileScreen(navController, backStackEntry, userGlobalConf)
        }
    }
}
