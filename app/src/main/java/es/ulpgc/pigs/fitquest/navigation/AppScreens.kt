package es.ulpgc.pigs.fitquest.navigation

sealed class AppScreens(val route: String){
    object WelcomeScreen : AppScreens("welcome_screen")
    object LoginScreen: AppScreens("login_screen")
    object SignupScreen: AppScreens("signup_screen")
    object MainMenuScreen: AppScreens("mainmenu_screen")
    object ProfileScreen: AppScreens("profile_screen")
    object SearchScreen : AppScreens("search_screen")
    object ChatListScreen : AppScreens("chatlist_screen")
    object ChatScreen : AppScreens("chat_screen/{username}") {
        fun createRoute(username: String) = "chat_screen/$username"
    }
    object GetFitScreen : AppScreens("getfit_screen")
    object GroupsScreen : AppScreens("groups_screen")
    object ShopScreen : AppScreens("shop_screen")
    object PremiumPaymentScreen : AppScreens("premiumpayment_screen")
}