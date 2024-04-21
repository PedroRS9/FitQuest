package es.ulpgc.pigs.fitquest.screens.signup

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import es.ulpgc.pigs.fitquest.R
import es.ulpgc.pigs.fitquest.components.ErrorDialog
import es.ulpgc.pigs.fitquest.data.Result
import es.ulpgc.pigs.fitquest.extensions.isValidEmail
import es.ulpgc.pigs.fitquest.extensions.isValidPassword
import es.ulpgc.pigs.fitquest.extensions.isValidUsername
import es.ulpgc.pigs.fitquest.ui.theme.FitquestTheme
import es.ulpgc.pigs.fitquest.components.FitquestButton
import es.ulpgc.pigs.fitquest.components.FitquestCheckbox
import es.ulpgc.pigs.fitquest.components.FitquestTextField
import es.ulpgc.pigs.fitquest.extensions.fitquestBackground
import es.ulpgc.pigs.fitquest.navigation.AppScreens

@Composable
fun SignupScreen(navController: NavController, backStackEntry: NavBackStackEntry){
    val viewModel: SignupViewModel = viewModel(backStackEntry)
    val signupState by viewModel.signupState.observeAsState()

    Column{
        BodyContent(
            navController = navController,
            onSignup = { username, email, password -> viewModel.onSignup(username,email,password) },
            clearErrors = { viewModel.clearError() },
            signupState = signupState
        )
    }
}

@Composable
fun BodyContent(navController: NavController,
                onSignup: (String, String, String) -> Unit,
                clearErrors: () -> Unit,
                signupState: Result?
) {
    var username by remember { mutableStateOf("") }
    var usernameError by remember { mutableStateOf<String?>(null) }
    var password by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    val showDialog = remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var termsAccepted by remember { mutableStateOf(false)}
    var termsError by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    Column(
        modifier = Modifier.fitquestBackground(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when(signupState) {
            is Result.GeneralSuccess -> navController.navigate(route = AppScreens.LoginScreen.route)
            is Result.Error -> {
                errorMessage = signupState.exception.message ?: ""
                showDialog.value = true
                clearErrors()
            }
            is Result.Loading -> {
                // we show the same as in LoginScreen.kt
                CircularProgressIndicator(
                    modifier = Modifier.width(100.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
                return
            }
            else -> {}
        }
        Image(
            painter = painterResource(R.drawable.fitquest_logo),
            contentDescription = stringResource(R.string.fitquest_logo_description),
            modifier = Modifier
                .size(200.dp)
        )
        FitquestTextField(
            value = username,
            onValueChange = {
                username = it
                usernameError = if(!username.isValidUsername()) context.getString(R.string.username_invalid_error) else null
            },
            label = stringResource(R.string.type_user),
            errorMessage = usernameError,
            isUser = true
        )
        FitquestTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = if (!email.isValidEmail()) context.getString(R.string.email_invalid_error) else null
            },
            label = stringResource(R.string.type_email),
            modifier = Modifier.padding(20.dp),
            errorMessage = emailError,
            isEmail = true
        )
        FitquestTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = if (!password.isValidPassword()) context.getString(R.string.password_invalid_error) else null
            },
            label = stringResource(R.string.type_password),
            isPassword = true,
            errorMessage = passwordError
        )
        FitquestCheckbox(
            checked = termsAccepted,
            onCheckedChange = {
                termsAccepted = it
                if(termsAccepted) termsError = null
            },
            label = stringResource(R.string.accept_tos)
        )
        if(termsError != null){
            Text(
                text = termsError ?: "",
                color = Color.Red,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        FitquestButton(
            onClick = {
                when {
                    !username.isValidUsername() -> usernameError = context.getString(R.string.username_invalid_error)
                    !email.isValidEmail() -> emailError = context.getString(R.string.email_invalid_error)
                    !password.isValidPassword() -> passwordError = context.getString(R.string.password_invalid_error)
                    !termsAccepted -> termsError = context.getString(R.string.tos_notaccepted_error)
                    else -> onSignup(username, email, password)
                }
            },
            text = stringResource(R.string.button_signup),
            modifier = Modifier.padding(20.dp),
        )
        if(showDialog.value){
            ErrorDialog(
                showDialog = showDialog,
                title = "Error",
                message = errorMessage,
                onDismiss = { showDialog.value = false }
            )
        }
    }
}

@Preview
@Composable
fun ShowPreview(){
    FitquestTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            BodyContent(
                navController = rememberNavController(),
                onSignup = { _, _, _ ->  },
                clearErrors = {},
                signupState = null
            )
        }
    }
}