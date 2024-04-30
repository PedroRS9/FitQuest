package es.ulpgc.pigs.fitquest.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Store
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem(AppScreens.MainMenuScreen.route, Icons.Default.Home, "Home")
    object GetFit : BottomNavItem(AppScreens.GetFitScreen.route, Icons.Default.Article, "Get Fit")
    object Groups : BottomNavItem(AppScreens.GroupsScreen.route, Icons.Default.Group, "Groups")
    object Shop : BottomNavItem(AppScreens.ShopScreen.route, Icons.Default.Store, "Shop")
    object Profile : BottomNavItem(AppScreens.ProfileScreen.route, Icons.Default.Person, "Profile")
}

fun getBottomNavItems(): List<BottomNavItem>{
    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.GetFit,
        BottomNavItem.Groups,
        BottomNavItem.Shop,
        BottomNavItem.Profile
    )
    return bottomNavItems
}