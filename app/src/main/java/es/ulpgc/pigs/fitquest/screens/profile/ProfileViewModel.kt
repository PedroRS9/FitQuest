package es.ulpgc.pigs.fitquest.screens.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.ulpgc.pigs.fitquest.data.FirebaseImageRepository
import es.ulpgc.pigs.fitquest.data.FirebaseUserRepository
import es.ulpgc.pigs.fitquest.data.ImageRepository
import es.ulpgc.pigs.fitquest.data.UserRepository
import es.ulpgc.pigs.fitquest.global.UserGlobalConf
import es.ulpgc.pigs.fitquest.data.Result
import es.ulpgc.pigs.fitquest.data.User

class ProfileViewModel() : ViewModel() {

    private val imageRepository: ImageRepository = FirebaseImageRepository()
    private val _imageState = MutableLiveData<Result>()
    val imageState: LiveData<Result> = _imageState

    private val userRepository: UserRepository = FirebaseUserRepository()
    private val _updateState = MutableLiveData<Result>()
    val updateState: LiveData<Result> = _updateState

    private lateinit var userGlobalConf: UserGlobalConf

    fun setUserGlobalConf(userGlobalConf: UserGlobalConf){
        this.userGlobalConf = userGlobalConf
    }

    fun onChooseImage(filename: String, byteArray: ByteArray, user: User){
        _imageState.value = Result.Loading
        imageRepository.uploadImage(filename, byteArray){ result: Result ->
            _imageState.value = result
            if(result is Result.ImageSuccess){
                userGlobalConf.currentUser.value!!.setPicture(result.bytes)
                user.setPictureURL("profilePictures/${filename}")
                onImageUploaded(user)
            }
        }
    }

    fun onImageUploaded(user: User){
        userRepository.updateUser(user){ result: Result ->
            _updateState.value = result
        }
    }

    fun checkIfPictureIsDownloaded(){
        val user = userGlobalConf.currentUser.value!!
        val userHasProfilePicture = user.getPictureURL() != null
        val pictureIsDownloaded = user.getPicture() != null
        if(userHasProfilePicture){
            if(pictureIsDownloaded){
                _imageState.value = Result.ImageSuccess(user.getPicture()!!)
            } else {
                downloadPicture(user.getPictureURL()!!)
            }
        }
    }

    fun downloadPicture(pictureURL: String){
        imageRepository.downloadImage(pictureURL){ result: Result ->
            if(result is Result.ImageSuccess){
                userGlobalConf.currentUser.value!!.setPicture(result.bytes)
            }
            _imageState.value = result
        }
    }

    fun clearError() {
        _imageState.value = null
    }
}