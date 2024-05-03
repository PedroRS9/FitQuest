package es.ulpgc.pigs.fitquest.screens.shopscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.ulpgc.pigs.fitquest.data.FirebaseShopRepository
import es.ulpgc.pigs.fitquest.data.FirebaseUserRepository
import es.ulpgc.pigs.fitquest.data.ShopRepository
import es.ulpgc.pigs.fitquest.data.Result
import es.ulpgc.pigs.fitquest.data.ShopItem
import es.ulpgc.pigs.fitquest.data.User
import es.ulpgc.pigs.fitquest.data.UserRepository

class ShopViewModel : ViewModel() {
    val shopRepository: ShopRepository = FirebaseShopRepository()
    val userRepository: UserRepository = FirebaseUserRepository()
    private val _shopState = MutableLiveData<Result>()
    val shopState: LiveData<Result> = _shopState
    suspend fun getShopItems() {
        _shopState.value = Result.Loading
        shopRepository.getItems { items ->
            _shopState.value = Result.ShopSuccess(items)
        }
    }

    fun onBuyItem(item: ShopItem, user: User) {
        // if the user has enough points, we buy the item
        if (user.getPoints() < item.price) {
            _shopState.value = Result.Error(Exception("You don't have enough points to buy this item"))
            return
        }
        user.setPoints(user.getPoints() - item.price)
        shopRepository.buyItem(item, user) { result ->
            if (result is Result.GeneralSuccess) {
                userRepository.updateUser(user){result ->
                    if (result is Result.GeneralSuccess) {
                        _shopState.value = Result.BuySuccess(true)
                    } else {
                        _shopState.value = Result.Error(Exception("Error buying item"))
                    }
                }
            } else {
                _shopState.value = Result.Error(Exception("Error buying item"))
            }
        }
    }

    fun setNormalState() {
        _shopState.value = Result.GeneralSuccess(true)
    }

}
