package es.ulpgc.pigs.fitquest.screens.getfit

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import es.ulpgc.pigs.fitquest.R
import es.ulpgc.pigs.fitquest.components.AchievementDialog
import es.ulpgc.pigs.fitquest.extensions.fitquestBackground
import es.ulpgc.pigs.fitquest.global.UserGlobalConf
import es.ulpgc.pigs.fitquest.navigation.BottomNavigationBar
import es.ulpgc.pigs.fitquest.navigation.TopNavigationBar

@ExperimentalMaterial3Api
@Composable
fun GetFitScreen(navController: NavController, userGlobalConf: UserGlobalConf) {
    Scaffold(
        topBar = { TopNavigationBar(navController, title = stringResource(R.string.topbar_getfit_title)) },
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        GetFitBodyContent(
            navController = navController,
            paddingValues = paddingValues
        )
    }
}

@Composable
fun GetFitBodyContent(navController: NavController, paddingValues: PaddingValues) {
    Column(
        modifier = Modifier
            .fitquestBackground()
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Canvas(modifier = Modifier
            .fillMaxWidth()
            .size(200.dp)) {
            drawRect(color = Color.Blue)
        }
        // we create a black background with a white text
        Box(contentAlignment = Alignment.Center){
            Canvas(modifier = Modifier
                .fillMaxWidth()
                .size(60.dp)) {
                drawRect(color = Color.Black)
            }
            Text(text = "MAIN NEWS TITLE", color = Color.White, fontSize = 40.sp)
        }

        LazyColumn {
            items(10) { index ->
                val rectangleHeight = 180.dp
                Row(modifier = Modifier.padding(10.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Canvas(modifier = Modifier
                            .width(100.dp)
                            .size(rectangleHeight)) {
                            drawRect(color = Color.Black)
                        }
                        Column(modifier = Modifier
                            .width(100.dp)
                            .height(rectangleHeight)
                            .padding(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "News $index",
                                fontSize = 20.sp,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = "Description of the news $index blablablablablablablablablablablablablablablablablablablablablabla",
                                fontSize = 14.sp,
                                color = Color.White,
                                maxLines = 5,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    Canvas(modifier = Modifier
                        .width(190.dp)
                        .size(rectangleHeight)
                    ){
                        drawRect(color = Color.Cyan)
                    }
                }
            }
        }



        Text(
            text = "COMING SOON!",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
        val showDialog = remember { mutableStateOf(false) }
        // Simular desbloqueo de logro
        Button(onClick = { showDialog.value = true }) {
            Text("Desbloquear logro")
        }
        if(showDialog.value){
            AchievementDialog {
                showDialog.value = false
            }
        }
    }
}

@ExperimentalMaterial3Api
@Preview
@Composable
fun PreviewGroupsScreen() {
    val navController = rememberNavController()
    Scaffold(
        topBar = { TopNavigationBar(navController, title = stringResource(R.string.topbar_groups_title)) },
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        GetFitBodyContent(
            navController = navController,
            paddingValues = paddingValues
        )
    }
}