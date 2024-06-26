package es.ulpgc.pigs.fitquest.navigation

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import es.ulpgc.pigs.fitquest.ui.theme.DarkGreen
import es.ulpgc.pigs.fitquest.ui.theme.SaturatedGreen

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar(
        containerColor = DarkGreen,
        contentColor = SaturatedGreen,
        modifier = Modifier.height(65.dp)
    ){
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val bottomNavItems = getBottomNavItems()
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = null, tint = Color.White, modifier = Modifier.offset(y = (-7).dp).size(30.dp)) },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = SaturatedGreen
                ),
                label = {
                    Text(item.label, color = Color.White, modifier = Modifier.offset(y = 2.dp))
                },
                alwaysShowLabel = true
            )
        }
    }
}