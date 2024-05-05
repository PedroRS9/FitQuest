package es.ulpgc.pigs.fitquest.screens.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.ulpgc.pigs.fitquest.data.Result
import es.ulpgc.pigs.fitquest.data.FirebaseImageRepository
import es.ulpgc.pigs.fitquest.data.FirebaseUserRepository
import es.ulpgc.pigs.fitquest.data.ImageRepository
import es.ulpgc.pigs.fitquest.data.SearchResult
import es.ulpgc.pigs.fitquest.data.UserRepository

class SearchViewModel : ViewModel(){

    val userRepository: UserRepository = FirebaseUserRepository()
    val imageRepository: ImageRepository = FirebaseImageRepository()
    private val _searchState = MutableLiveData<SearchResult>()
    val searchState: LiveData<SearchResult> = _searchState

    fun onSearch(query: String) {
        _searchState.value = SearchResult.Loading
        userRepository.searchUsers(query){ result ->
            _searchState.value = result
            if(result is SearchResult.Results){
                // for each user in the list, we download the profile picture
                val updatedResults = result.results
                updatedResults.forEach { user ->
                    if(user.getPictureURL() != null){
                        imageRepository.downloadImage(user.getPictureURL()!!){ imageResult ->
                            if(imageResult is Result.ImageSuccess){
                                user.setPicture(imageResult.bytes)
                                _searchState.value = null // to trigger recomposition
                                _searchState.value = result
                            }
                        }
                    }
                }
            } else{
                _searchState.value = result
            }
        }
    }

}