package es.ulpgc.pigs.fitquest.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import es.ulpgc.pigs.fitquest.ui.theme.DarkGreen

@ExperimentalMaterial3Api
@Composable
fun TopNavigationBar(navController: NavController, title: String) {
    TopAppBar(
        title = { Text(text = title) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = DarkGreen,
            titleContentColor = Color.White
        ),
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Go back"
                )
            }
        },
        actions = {
            IconButton(onClick = { navController.navigate(AppScreens.SearchScreen.route) }) {
                Icon(Icons.Filled.Search, contentDescription = "Search Profile", tint = Color.White)
            }
            IconButton(onClick = { navController.navigate(AppScreens.ChatScreen.route) }){
                Icon(Icons.Filled.Chat, contentDescription = "Chat", tint = Color.White)
            }
        }
    )
}