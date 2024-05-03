package es.ulpgc.pigs.fitquest.screens.chatscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import es.ulpgc.pigs.fitquest.R
import es.ulpgc.pigs.fitquest.data.User
import es.ulpgc.pigs.fitquest.extensions.fitquestBackground
import es.ulpgc.pigs.fitquest.global.UserGlobalConf
import es.ulpgc.pigs.fitquest.navigation.BottomNavigationBar
import es.ulpgc.pigs.fitquest.navigation.TopNavigationBar
import es.ulpgc.pigs.fitquest.data.Result
import es.ulpgc.pigs.fitquest.navigation.AppScreens

@ExperimentalMaterial3Api
@Composable
fun ChatListScreen(navController: NavController, backStackEntry: NavBackStackEntry, userGlobalConf: UserGlobalConf){
    val user by userGlobalConf.currentUser.observeAsState()
    val viewModel: ChatListViewModel = viewModel(backStackEntry)
    val chatListState by viewModel.chatListState.observeAsState()
    LaunchedEffect(Unit){
        viewModel.getDoctors()
    }
    Scaffold(
        topBar = { TopNavigationBar(navController, title = stringResource(R.string.topbar_chat_title)) },
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        ChatListBody(
            navController = navController,
            paddingValues = paddingValues,
            chatListState = chatListState
        )
    }
}

@Composable
fun ChatListBody(navController: NavController, paddingValues: PaddingValues, chatListState: Result?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .fitquestBackground()
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when(chatListState){
            is Result.ChatListSuccess -> {
                val users = chatListState.users
                for (user in users){
                    val painter = if (user.hasProfilePicture()) {
                        rememberAsyncImagePainter(user.getPicture()!!)
                    } else {
                        painterResource(id = R.drawable.default_profile_pic)
                    }
                    ChatListItem(profilePicture = painter, username = user.getName(),
                        message = "hola buenas", onClick = { navController.navigate(AppScreens.ChatScreen.createRoute(user.getName())) })
                }
            }
            is Result.Error -> {
                Text(text = "Error", fontSize = 30.sp, color = Color.Red)
            }
            is Result.Loading -> {
                Spacer(modifier = Modifier.height(300.dp))
                CircularProgressIndicator(
                    modifier = Modifier.width(100.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
            else -> {}
        }

    }
}

@Composable
fun ChatListItem(profilePicture: Painter, username: String, message: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(Color.Gray)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ){
        Spacer(modifier = Modifier.width(10.dp))
        Image(
            painter = profilePicture,
            contentDescription = "Imagen de perfil"
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column{
            Text(text = username, fontSize = 30.sp,  color= Color.White)
            Text(text = message, fontSize = 15.sp, color= Color.White, modifier = Modifier.offset(x = 2.dp, y = -4.dp))
        }
    }
}

@Preview
@ExperimentalMaterial3Api
@Composable
fun ChatListScreenPreview() {
    val navController: NavController = rememberNavController()
    Scaffold(
        topBar = { TopNavigationBar(navController = navController, title = "Chat") },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->
        ChatListBody(
            navController = navController,
            paddingValues = paddingValues,
            chatListState = Result.ChatListSuccess(listOf(User(name = "User", password = "", email="", isDoctor=true)))
        )
    }
}