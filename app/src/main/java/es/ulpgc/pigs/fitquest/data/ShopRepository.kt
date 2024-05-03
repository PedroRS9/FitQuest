package es.ulpgc.pigs.fitquest.data

interface ShopRepository {
    suspend fun getItems(callback: (List<ShopItem>) -> Unit)
    fun buyItem(shopItem: ShopItem, user: User, callback: (Result) -> Unit)
}
