package es.ulpgc.pigs.fitquest.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import es.ulpgc.pigs.fitquest.global.UserGlobalConf
import es.ulpgc.pigs.fitquest.screens.chatscreen.ChatListScreen
import es.ulpgc.pigs.fitquest.screens.chatscreen.ChatScreen
import es.ulpgc.pigs.fitquest.screens.loginscreen.LoginScreen
import es.ulpgc.pigs.fitquest.screens.mainmenu.MainMenuScreen
import es.ulpgc.pigs.fitquest.screens.profile.ProfileScreen
import es.ulpgc.pigs.fitquest.screens.search.SearchScreen
import es.ulpgc.pigs.fitquest.screens.shopscreen.ShopScreen
import es.ulpgc.pigs.fitquest.screens.signup.SignupScreen
import es.ulpgc.pigs.fitquest.screens.welcomemenu.WelcomeScreen

@ExperimentalMaterial3Api
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
        composable(route = AppScreens.SearchScreen.route){ backStackEntry ->
            SearchScreen(navController, backStackEntry)
        }
        composable(route = AppScreens.ProfileScreen.route){ backStackEntry ->
            ProfileScreen(navController, backStackEntry, userGlobalConf)
        }
        composable(route = AppScreens.ChatListScreen.route){ backStackEntry ->
            ChatListScreen(navController, backStackEntry, userGlobalConf)
        }
        composable(route = AppScreens.ChatScreen.route,
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        ){ backStackEntry ->
            val username = backStackEntry.arguments?.getString("username")
            ChatScreen(navController, backStackEntry, userGlobalConf, username)
        }
        // a composable for the shop screen
        composable(route = AppScreens.ShopScreen.route){ backStackEntry ->
            ShopScreen(navController, backStackEntry, userGlobalConf)
        }
    }
}
