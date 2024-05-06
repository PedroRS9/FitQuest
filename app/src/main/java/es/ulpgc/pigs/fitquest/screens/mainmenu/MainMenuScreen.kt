package es.ulpgc.pigs.fitquest.screens.mainmenu

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import es.ulpgc.pigs.fitquest.R
import es.ulpgc.pigs.fitquest.components.GradientCircularProgressIndicator
import es.ulpgc.pigs.fitquest.data.User
import es.ulpgc.pigs.fitquest.extensions.fitquestHomeBackground
import es.ulpgc.pigs.fitquest.extensions.fitquestLoginBackground
import es.ulpgc.pigs.fitquest.global.UserGlobalConf
import es.ulpgc.pigs.fitquest.navigation.AppScreens
import es.ulpgc.pigs.fitquest.navigation.BottomNavigationBar
import es.ulpgc.pigs.fitquest.navigation.TopNavigationBar
import es.ulpgc.pigs.fitquest.ui.theme.FitquestTheme
import es.ulpgc.pigs.fitquest.ui.theme.LightGrey
import java.time.format.TextStyle

@ExperimentalMaterial3Api
@Composable
fun MainMenuScreen(navController: NavController, backStackEntry: NavBackStackEntry, userGlobalConf: UserGlobalConf) {
    val showDialog = remember { mutableStateOf(false) }
    val user by userGlobalConf.currentUser.observeAsState()
    val viewModel: MainMenuViewModel = viewModel(backStackEntry)
    viewModel.setUserGlobalConf(userGlobalConf)
    val context = LocalContext.current
    LaunchedEffect(user){
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

    Scaffold(
        topBar = { TopNavigationBar(navController = navController, title = stringResource(R.string.topbar_mainmenu_title)) },
        bottomBar = { BottomNavigationBar(navController) },
    ) { paddingValues ->
        StepCounterScreen(stepState = stepState,
            user = user,
            stepDialogState = stepDialogState,
            setStepGoal = { user, stepGoal -> viewModel.setStepGoal(user, stepGoal) },
            requestStepGoalChange = { viewModel.requestStepGoalChange() },
            stepReset = { viewModel.resetSteps() },
            paddingValues = paddingValues)
    }
}

@Composable
fun StepCounterScreen(
    stepState: State<Int>,
    user: User?,
    stepDialogState: State<Boolean>,
    setStepGoal: (User, Int) -> Unit,
    requestStepGoalChange: () -> Unit,
    stepReset: () -> Unit,
    paddingValues: PaddingValues
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
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(top = 50.dp)
            .fitquestHomeBackground(),
        ) {
            val stepsFloat = stepState.value?.toFloat()
            val stepGoal = user?.getStepGoal()?.toFloat()
            val gradientBrush = Brush.radialGradient(
                colors = listOf(Color.Red, Color.Yellow), // Lista de colores del gradiente
                center = Offset(100f, 100f), // Centro del gradiente (opcional)
                radius = 200f // Radio del gradiente (opcional)
            )

            GradientCircularProgressIndicator(
                progress = stepsFloat?.div(stepGoal ?: 1f) ?: 0f,
                modifier = Modifier
                    .size(320.dp)
                    .align(Alignment.TopCenter),
                strokeWidth = 35f,
                trackColor = Color.LightGray,
                colors = listOf(
                    Color(0xFFFF0000), // Rojo
                    Color(0xFFFF5722), // Naranja Rojizo
                    Color(0xFFFFC107), // Ámbar
                    Color(0xFFFFEB3B), // Amarillo
                    Color(0xFFCDDC39), // Lima
                    Color(0xFF8BC34A),  // Verde claro
                    Color(0xFF4CAF50)  // Verde
                )
            )
            Text(
                text = "GOAL ${user?.getStepGoal()}",
                modifier = Modifier.align(Alignment.Center)
                    .offset(y = (-220).dp),
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    color = Color(android.graphics.Color.parseColor("#00FF00"))
                )
            )
            Text(
                //text = "${stepState.value}",
                text = "25899",
                modifier = Modifier.align(Alignment.Center)
                    .offset(y = (-150).dp),
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 72.sp,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold
                )
            )

            Text(
                text = "STEPS",
                modifier = Modifier.align(Alignment.Center)
                    .offset(y = (-80).dp),
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    color = Color.LightGray
                )
            )
            /*
        Button(
            onClick = { stepReset() },
            modifier = Modifier.padding(10.dp)
        ) {
            Text("Reset Steps")
        }
         */
            Button(
                onClick = { requestStepGoalChange() },
                modifier = Modifier.padding(10.dp)
                    .align(Alignment.Center)
                    .offset(y = (80).dp)
            ) {
                Text("Set new goal")
            }

            Box(
                modifier = Modifier
                    .width(320.dp)
                    .height(160.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(android.graphics.Color.parseColor("#DEDEDE")))
                    .align(Alignment.BottomCenter)
                    .offset(y = (-20).dp)
                    .padding(bottom = 30.dp)

            ) {
                Text(
                    text = "WEIGHT",
                    color = Color.Black,
                    fontSize = 25.sp,
                    modifier = Modifier.padding(30.dp)

                    )
            }

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
                    paddingValues = paddingValues,
                    stepDialogState = stepDialogState,
                    stepReset = { },
                    setStepGoal = { user, stepGoal -> },
                    requestStepGoalChange = { }
                )
            }
        }
    }
}