package es.ulpgc.pigs.fitquest.screens.chatscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.ulpgc.pigs.fitquest.data.FirebaseImageRepository
import es.ulpgc.pigs.fitquest.data.FirebaseUserRepository
import es.ulpgc.pigs.fitquest.data.ImageRepository
import es.ulpgc.pigs.fitquest.data.UserRepository
import es.ulpgc.pigs.fitquest.data.Result
import es.ulpgc.pigs.fitquest.data.User

class ChatListViewModel : ViewModel() {
    private val UserRepository: UserRepository = FirebaseUserRepository()
    private val ImageRepository: ImageRepository = FirebaseImageRepository()
    private val _chatListState = MutableLiveData<Result>()
    val chatListState: LiveData<Result> = _chatListState

    fun getDoctors(){
        _chatListState.value = Result.Loading
        UserRepository.getAllDoctors { result: List<User> ->
            // for each user in the list, we download the profile picture
            val updatedResults = result
            updatedResults.forEach { user ->
                if(user.getPictureURL() != null){
                    ImageRepository.downloadImage(user.getPictureURL()!!){ imageResult ->
                        if(imageResult is Result.ImageSuccess){
                            user.setPicture(imageResult.bytes)
                        }
                    }
                }
            }
            _chatListState.value = Result.ChatListSuccess(updatedResults)
        }
    }
}