package es.ulpgc.pigs.fitquest.screens.profile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import es.ulpgc.pigs.fitquest.components.FitquestProfilePicture
import es.ulpgc.pigs.fitquest.data.User
import es.ulpgc.pigs.fitquest.extensions.fitquestBackground
import es.ulpgc.pigs.fitquest.global.UserGlobalConf
import coil.compose.rememberAsyncImagePainter
import es.ulpgc.pigs.fitquest.R
import es.ulpgc.pigs.fitquest.components.AchievementCard
import es.ulpgc.pigs.fitquest.components.ErrorDialog
import es.ulpgc.pigs.fitquest.components.ExperienceBar
import es.ulpgc.pigs.fitquest.data.Achievement
import es.ulpgc.pigs.fitquest.data.Result
import es.ulpgc.pigs.fitquest.navigation.BottomNavigationBar
import es.ulpgc.pigs.fitquest.navigation.TopNavigationBar
import es.ulpgc.pigs.fitquest.ui.theme.LightGrey
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.InputStream


@ExperimentalMaterial3Api
@Composable
fun ProfileScreen(navController: NavController, backStackEntry: NavBackStackEntry, userGlobalConf: UserGlobalConf, anotherUsername: String? = null){
    val user by userGlobalConf.currentUser.observeAsState()
    val anotherUser = User(name = anotherUsername ?: "", password = "", email = "", isDoctor = false, xp=0, level=0) // it will be downloaded later
    val viewModel: ProfileViewModel = viewModel(backStackEntry)
    viewModel.setUserGlobalConf(userGlobalConf)
    LaunchedEffect(Unit){
        if(anotherUsername == null){
            viewModel.checkIfPictureIsDownloaded(viewModel.getUserGlobalConf().currentUser.value!!)
            viewModel.getUserAchievements(user!!)
        } else{
            viewModel.downloadAnotherUser(anotherUsername){ u ->
                viewModel.viewModelScope.launch {
                    viewModel.getUserAchievements(u)
                }
            }
        }
    }
    val imageState by viewModel.imageState.observeAsState()
    val updateState by viewModel.updateState.observeAsState()
    val achievementState by viewModel.achievementState.observeAsState()
    Scaffold(
        topBar = { TopNavigationBar(navController, title = stringResource(R.string.topbar_profile_title)) },
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        BodyContent(
            user = if(anotherUsername == null) user!! else anotherUser,
            uploadImage = { filename: String, byteArray: ByteArray, us: User ->
                viewModel.onChooseImage(filename, byteArray, us)
            },
            clearViewModel = { viewModel.clearError()},
            imageState = imageState,
            updateState = updateState,
            achievementState = achievementState,
            paddingValues = paddingValues,
            isAnotherUser = anotherUsername != null
        )
    }
}

@Composable
fun BodyContent(
    user: User,
    uploadImage: (String, ByteArray, User) -> Unit,
    clearViewModel: () -> Unit,
    imageState: Result?,
    updateState: Result?,
    achievementState: Result?,
    paddingValues: PaddingValues,
    isAnotherUser: Boolean
){
    val painter = when (imageState) {
        is Result.ImageSuccess -> { rememberAsyncImagePainter(imageState.bytes) }
        else -> painterResource(id = R.drawable.default_profile_pic)
    }
    val showDialog = remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var userAchievements by remember { mutableStateOf(listOf<Achievement>()) }
    var userValue by remember { mutableStateOf(user) }
    val context = LocalContext.current
    if(achievementState is Result.AchievementSuccess){
        userAchievements = achievementState.achievements
    }
    Column(
        modifier = Modifier
            .fitquestBackground()
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let {
                val inputStream: InputStream? = context.contentResolver.openInputStream(it)
                inputStream?.let { stream ->
                    val bitmap = BitmapFactory.decodeStream(stream)
                    val outputStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    val bytes = outputStream.toByteArray()
                    uploadImage("${userValue.getName()}.jpg", bytes, userValue)
                }
            }
        }
        Box(contentAlignment = Alignment.Center){

        }
        when(imageState){
            is Result.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.width(100.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
            else -> {
                FitquestProfilePicture(userProfileImage = painter, isChangeable = !isAnotherUser, modifier = Modifier.padding(20.dp),
                    onUploadImageClick = {
                        launcher.launch("image/*")
                    }
                )
            }
        }
        Text(text = userValue.getName(), color = Color.Black, fontSize = 30.sp)
        Text(text = "Level ${userValue.getLevel()}", color = Color.Black, fontSize = 20.sp)
        ExperienceBar(userValue.calculateXpPercentage(), modifier = Modifier.padding(20.dp))
        Column(
            modifier = Modifier
                .size(340.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(LightGrey),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "ACHIEVEMENTS",
                color = Color.Black,
                fontSize = 25.sp,
                modifier = Modifier.padding(10.dp)
            )
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
                horizontalAlignment = Alignment.Start){
                for(achievement in userAchievements){
                    AchievementCard(title = achievement.title,
                        description = achievement.description,
                        //image = rememberAsyncImagePainter(achievement.image)
                        image = rememberAsyncImagePainter(model = achievement.image)
                    )
                }
            }

        }

        if(showDialog.value){
            ErrorDialog(
                showDialog = showDialog,
                title = "Error",
                message = errorMessage,
                onDismiss = { showDialog.value = false }
            )
        }

        when(updateState){
            is Result.GeneralSuccess -> {}
            is Result.Error -> {
                errorMessage = updateState.exception.message ?: "Error desconocido"
                showDialog.value = true
                clearViewModel()
            }
            is Result.LoginSuccess -> userValue = updateState.user
            else -> {}
        }
    }
}

@ExperimentalMaterial3Api
@Preview
@Composable
fun ShowPreview(){
    val user = User(name = "Paquito", password = "", email = "", isDoctor = false, xp=50, level=1)
    val updateState = Result.GeneralSuccess(true)
    val navController: NavController = rememberNavController()
    val byteArray = createBlueBitmapByteArray(1000, 1000)
    val achievements = listOf(
        Achievement(id = "", title = "Achievement 1", description = "Description 1", image = byteArray, category = ""),
        Achievement(id = "", title = "Achievement 2", description = "Description 2", image = byteArray, category = "")
    )
    Scaffold(
        topBar = { TopNavigationBar(navController, title = stringResource(R.string.topbar_profile_title)) },
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        BodyContent(
            user = user,
            uploadImage = { filename: String, byteArray: ByteArray, us: User -> },
            clearViewModel = {},
            imageState = null,
            updateState = updateState,
            achievementState = Result.AchievementSuccess(achievements),
            paddingValues = paddingValues,
            isAnotherUser = false
        )
    }
}

fun createBlueBitmapByteArray(width: Int, height: Int): ByteArray {
    // Crea un Bitmap mutable con el color azul
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    // Dibujar un rectángulo rojo en el Bitmap
    canvas.drawColor(android.graphics.Color.BLUE)

    // Convertir el Bitmap a un array de bytes
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)  // Comprimir el bitmap como PNG
    val byteArray = stream.toByteArray()
    stream.close()

    return byteArray
}