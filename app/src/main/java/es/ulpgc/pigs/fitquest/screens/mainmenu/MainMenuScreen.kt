package es.ulpgc.pigs.fitquest.screens.mainmenu

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import es.ulpgc.pigs.fitquest.R
import es.ulpgc.pigs.fitquest.components.AchievementDialog
import es.ulpgc.pigs.fitquest.data.User
import es.ulpgc.pigs.fitquest.global.UserGlobalConf
import es.ulpgc.pigs.fitquest.navigation.AppScreens
import es.ulpgc.pigs.fitquest.navigation.BottomNavigationBar
import es.ulpgc.pigs.fitquest.navigation.TopNavigationBar
import es.ulpgc.pigs.fitquest.ui.theme.FitquestTheme
import es.ulpgc.pigs.fitquest.data.Result
import es.ulpgc.pigs.fitquest.extensions.playSound

@ExperimentalMaterial3Api
@Composable
fun MainMenuScreen(navController: NavController, backStackEntry: NavBackStackEntry, userGlobalConf: UserGlobalConf) {
    val showDialog = remember { mutableStateOf(false) }
    val user by userGlobalConf.currentUser.observeAsState()
    val viewModel: MainMenuViewModel = viewModel(backStackEntry)
    viewModel.setUserGlobalConf(userGlobalConf)
    val context = LocalContext.current
    LaunchedEffect(user){
        viewModel.setUser(user!!)
        viewModel.checkEnteredApplicationAchievement()
        viewModel.initSensor(context)
        viewModel.checkStepGoal(user!!)
    }
    BackHandler {
        showDialog.value = true
    }
    if (showDialog.value) {
        ConfirmLogoutDialog(showDialog = showDialog, navController)
    }

    val stepState = viewModel.steps.observeAsState(0)
    val stepDialogState = viewModel.showStepGoalDialog.observeAsState(false)
    val achievementState = viewModel.achievementState.observeAsState(null)

    Scaffold(
        topBar = { TopNavigationBar(navController = navController, title = stringResource(R.string.topbar_mainmenu_title)) },
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        StepCounterScreen(stepState = stepState,
            user = user,
            stepDialogState = stepDialogState,
            setStepGoal = { user, stepGoal -> viewModel.setStepGoal(user, stepGoal) },
            requestStepGoalChange = { viewModel.requestStepGoalChange() },
            stepReset = { viewModel.resetSteps() },
            achievementState = achievementState,
            clearAchievementState = { viewModel.clearAchievementState() },
            paddingValues = paddingValues)
    }
}

@Composable
fun StepCounterScreen(
    stepState: State<Int>,
    user: User?,
    stepDialogState: State<Boolean>,
    achievementState: State<Result?>,
    setStepGoal: (User, Int) -> Unit,
    requestStepGoalChange: () -> Unit,
    stepReset: () -> Unit,
    paddingValues: PaddingValues,
    clearAchievementState: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (stepDialogState.value) {
            SetStepGoalDialog(user = user!!, onConfirm = setStepGoal)
            return
        }
        if(achievementState.value != null && achievementState.value is Result.AchievementSuccess){
            clearAchievementState()
            val achievement = (achievementState.value as Result.AchievementSuccess).achievements[0]
            AchievementDialog(achievementImage = rememberAsyncImagePainter(model = achievement.image),
                title = achievement.title,
                description = achievement.description,
                onDismiss = { }
            )
            playSound(LocalContext.current, R.raw.achievement_win_fanfare)
        }

        Box(modifier = Modifier
            .size(360.dp)
            .padding(20.dp)
        ) {
            val stepsFloat = stepState.value?.toFloat()
            val stepGoal = user?.getStepGoal()?.toFloat()

            CircularProgressIndicator(
                progress = stepsFloat?.div(stepGoal ?: 1f) ?: 0f,
                modifier = Modifier
                    .size(320.dp)
                    .padding(50.dp),
                strokeWidth = 20.dp,
                trackColor = Color.Gray,
                color = Color(android.graphics.Color.parseColor("#00FF00"))
            )
            Text(
                text = "Steps: ${stepState.value}",
                modifier = Modifier.align(Alignment.Center)
            )
            Text(
                text = "Goal: ${user?.getStepGoal()} steps",
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        Button(
            onClick = { stepReset() },
            modifier = Modifier.padding(10.dp)
        ) {
            Text("Reset Steps")
        }
        Button(
            onClick = { requestStepGoalChange() },
            modifier = Modifier.padding(10.dp)
        ) {
            Text("Set new goal")
        }
    }
}

@Composable
fun SetStepGoalDialog(user: User, onConfirm: (User, Int) -> Unit) {
    var stepGoalText by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = {},
        title = { Text("Set Step Goal") },
        text = {
            TextField(
                value = stepGoalText,
                onValueChange = { stepGoalText = it },
                label = { Text("Enter your step goal") }
            )
        },
        confirmButton = {
            Button(onClick = { onConfirm(user, stepGoalText.toInt()) }){
                Text("Set Goal")
            }
        },
        dismissButton = {
            Button(onClick = {}) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ConfirmLogoutDialog(showDialog: MutableState<Boolean>, navController: NavController ) {
    AlertDialog(
        onDismissRequest = {
            showDialog.value = false
        },
        title = { Text("Confirmation") },
        text = { Text("Do you want to log out?") },
        confirmButton = {
            TextButton(
                onClick = {
                    showDialog.value = false
                    navController.navigate(route = AppScreens.LoginScreen.route)
                }
            ) {
                Text("Yes")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    showDialog.value = false
                }
            ) {
                Text("No")
            }
        }
    )
}


@Preview
@ExperimentalMaterial3Api
@Composable
fun ShowPreview() {
    val stepState = remember { mutableIntStateOf(500) }
    val stepDialogState = remember { mutableStateOf(false) }
    val achievementState = remember { mutableStateOf<Result?>(null) }
    FitquestTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                topBar = { TopNavigationBar(navController = rememberNavController(), title = stringResource(R.string.topbar_mainmenu_title)) },
                bottomBar = { BottomNavigationBar(rememberNavController()) }
            ) { paddingValues ->
                StepCounterScreen(
                    stepState = stepState,
                    user = User(name="Paquito", password="", email="", isDoctor=false, steps=500, stepGoal=1000),
                    stepDialogState = stepDialogState,
                    achievementState = achievementState,
                    setStepGoal = { user, stepGoal -> },
                    requestStepGoalChange = { },
                    stepReset = { },
                    paddingValues = paddingValues,
                    clearAchievementState = { }
                )
            }
        }
    }
}