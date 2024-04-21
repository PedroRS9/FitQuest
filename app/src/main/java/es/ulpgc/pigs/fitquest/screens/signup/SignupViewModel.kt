package es.ulpgc.pigs.fitquest.screens.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.ulpgc.pigs.fitquest.data.FirebaseUserRepository
import es.ulpgc.pigs.fitquest.data.User
import es.ulpgc.pigs.fitquest.data.Result
import es.ulpgc.pigs.fitquest.data.UserRepository

class SignupViewModel : ViewModel() {
    private val userRepository: UserRepository = FirebaseUserRepository()
    private val _signupState = MutableLiveData<Result>()
    val signupState: LiveData<Result> = _signupState
    fun onSignup(username: String, email: String, password: String) {
        val user = User(name = username, email = email, password = password)
        _signupState.value = Result.Loading
        userRepository.createUser(user){ result: Result ->
            _signupState.value = result
        }
    }

    fun clearError(){
        _signupState.value = null
    }
}