package es.ulpgc.pigs.fitquest.navigation

sealed class AppScreens(val route: String){
    object WelcomeScreen : AppScreens("welcome_screen")
    object LoginScreen: AppScreens("login_screen")
    object SignupScreen: AppScreens("signup_screen")
    object MainMenuScreen: AppScreens("mainmenu_screen")
    object ProfileScreen: AppScreens("profile_screen")
    object SearchScreen : AppScreens("search_screen")
}