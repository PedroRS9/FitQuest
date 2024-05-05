package es.ulpgc.pigs.fitquest.screens.getfit

import android.graphics.fonts.FontStyle
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import es.ulpgc.pigs.fitquest.R
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
        GroupsBodyContent(
            navController = navController,
            paddingValues = paddingValues
        )
    }
}

@Composable
fun GroupsBodyContent(navController: NavController, paddingValues: PaddingValues) {
    Column(
        modifier = Modifier
            .fitquestBackground()
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text(
            text = "COMING SOON!",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
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
        GroupsBodyContent(
            navController = navController,
            paddingValues = paddingValues
        )
    }
}