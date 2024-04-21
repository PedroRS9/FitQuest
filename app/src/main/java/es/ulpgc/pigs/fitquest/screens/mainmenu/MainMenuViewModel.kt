package es.ulpgc.pigs.fitquest.screens.mainmenu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.ulpgc.pigs.fitquest.data.FirebaseImageRepository
import es.ulpgc.pigs.fitquest.data.ImageRepository
import es.ulpgc.pigs.fitquest.data.Result
import es.ulpgc.pigs.fitquest.global.UserGlobalConf

class MainMenuViewModel() : ViewModel() {
    private val imageRepository: ImageRepository = FirebaseImageRepository()
    private val _imageState = MutableLiveData<Result>()
    val imageState: LiveData<Result> = _imageState

    private lateinit var userGlobalConf: UserGlobalConf

    fun setUserGlobalConf(userGlobalConf: UserGlobalConf){
        this.userGlobalConf = userGlobalConf
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
}