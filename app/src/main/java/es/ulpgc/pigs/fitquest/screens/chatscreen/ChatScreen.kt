package es.ulpgc.pigs.fitquest.screens.chatscreen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import es.ulpgc.pigs.fitquest.components.MessageBubble
import es.ulpgc.pigs.fitquest.data.User
import es.ulpgc.pigs.fitquest.global.UserGlobalConf
import es.ulpgc.pigs.fitquest.navigation.BottomNavigationBar
import es.ulpgc.pigs.fitquest.navigation.TopNavigationBar
import es.ulpgc.pigs.fitquest.data.Message
import es.ulpgc.pigs.fitquest.data.Result
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.LaunchedEffect
import androidx.constraintlayout.compose.Dimension
import es.ulpgc.pigs.fitquest.components.ChatBox
import java.util.Date


@ExperimentalMaterial3Api
@Composable
fun ChatScreen(navController: NavController, backStackEntry: NavBackStackEntry, userGlobalConf: UserGlobalConf, receiverUsername: String?){
    val user by userGlobalConf.currentUser.observeAsState()
    val viewModel: ChatViewModel = viewModel(backStackEntry)
    val chatState by viewModel.chatState.observeAsState()

    val userIsDoctor = user?.isDoctor() ?: false
    LaunchedEffect(Unit){
        viewModel.getChatId(
            doctorId = if(userIsDoctor) user?.getName() ?: "" else receiverUsername ?: "",
            userId = if(userIsDoctor) receiverUsername ?: "" else user?.getName() ?: ""
            )
    }
    Scaffold(
        topBar = { TopNavigationBar(navController, title = receiverUsername ?: "") },
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        BodyContent(
            sender = user,
            chatState = chatState,
            onSend = { message: String -> viewModel.sendMessage(user?.getName() ?: "", message) },
            paddingValues = paddingValues
        )
    }
}

@Composable
fun BodyContent(sender: User?, chatState: Result?, onSend: (String) -> Unit, paddingValues: PaddingValues) {
    when (chatState) {
        is Result.Loading, null -> {
            CircularProgressIndicator(
                modifier = Modifier.width(100.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
            return
        }
        is Result.Error -> {
            Text(text = "Error", color = Color.Black, fontSize = 24.sp)
            return
        }
        else -> {}
    }
    // if it is not loading or error, it must be a success

    ConstraintLayout(modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)) {
        val (messages, chatBox) = createRefs()
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(messages) {
                    top.linkTo(parent.top)
                    bottom.linkTo(chatBox.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    height = Dimension.fillToConstraints
                }
        ){
            val messagesList = (chatState as Result.ChatSuccess).messages
            items(messagesList) { item ->
                val itsMyMessage = item.itsMyMessage(sender?.getName() ?: "")
                MessageBubble(message = item, itsMyMessage = itsMyMessage)
            }
        }
        ChatBox(onSend = onSend, modifier = Modifier
            .fillMaxWidth()
            .constrainAs(chatBox) {
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            })
    }
}



@ExperimentalMaterial3Api
@Preview
@Composable
fun PreviewChatScreen(){
    val navController = rememberNavController()
    val messages = ArrayList<Message>()
    // the timestamp, which is a Date object
    messages.add(Message("Hola", "PedroRS9", timestamp = Date()))
    messages.add(Message("Hola", "Paco", timestamp = Date()))

    Scaffold(
        topBar = { TopNavigationBar(navController, title = "Paco") },
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        BodyContent(
            sender = User(name="PedroRS9",email="pedro.romero105@alu.ulpgc.es", password="", isDoctor=false),
            chatState = Result.ChatSuccess(messages),
            onSend = { },
            paddingValues = paddingValues
        )
    }
}