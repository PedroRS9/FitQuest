package es.ulpgc.pigs.fitquest.screens.loginscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import es.ulpgc.pigs.fitquest.data.FirebaseUserRepository
import es.ulpgc.pigs.fitquest.data.UserRepository
import java.lang.Exception
import es.ulpgc.pigs.fitquest.data.Result
import es.ulpgc.pigs.fitquest.data.User

class LoginViewModel : ViewModel() {

    private val userRepository: UserRepository = FirebaseUserRepository()
    private val _loginState = MutableLiveData<Result>()
    val loginState: LiveData<Result> = _loginState

    fun onLogin(usernameOrEmail: String, password: String) {
        if (usernameOrEmail.isBlank() || password.isBlank()) {
            _loginState.value = Result.Error(Exception("An email and password are required."))
            return
        }

        val isEmail = usernameOrEmail.contains("@")
        _loginState.value = Result.Loading
        val userQueryCallback: (User?) -> Unit = { user ->
            if (user == null) {
                _loginState.postValue(Result.Error(Exception("User not found")))
            } else {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(user.getEmail(), password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            _loginState.postValue(Result.LoginSuccess(user))
                        } else {
                            _loginState.postValue(Result.Error(Exception("Auth error")))
                        }
                    }
            }
        }

        if (isEmail) {
            userRepository.findUserByEmail(usernameOrEmail, userQueryCallback)
        } else {
            userRepository.findUserByUsername(usernameOrEmail, userQueryCallback)
        }
    }

    fun clearError() {
        _loginState.value = null
    }
}
