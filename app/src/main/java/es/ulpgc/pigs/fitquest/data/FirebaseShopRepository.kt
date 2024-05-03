package es.ulpgc.pigs.fitquest.data

import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

class FirebaseShopRepository : ShopRepository{
    val database = Firebase.firestore
    val storageReference = Firebase.storage.reference
    override suspend fun getItems(callback: (List<ShopItem>) -> Unit) {
        coroutineScope {
            val result = database.collection("shop").get().await()
            val items = result.documents.mapNotNull { doc ->
                async {
                    val name = doc.getString("name") ?: ""
                    val price = doc.getLong("price")?.toInt() ?: 0
                    val description = doc.getString("description") ?: ""
                    val pictureURL = doc.getString("pictureURL") ?: ""
                    val imageRef = storageReference.child(pictureURL)
                    val ONE_MEGABYTE: Long = 1024 * 1024
                    try {
                        val bytes = imageRef.getBytes(ONE_MEGABYTE).await()
                        ShopItem(id = doc.id, name = name, price = price, description = description, image = bytes)
                    } catch (e: Exception) {
                        null // En caso de error en la descarga de la imagen, puedes decidir cómo manejarlo.
                    }
                }
            }.awaitAll().filterNotNull()
            callback(items)
        }
    }

    override fun buyItem(shopItem: ShopItem, user: User, callback: (Result) -> Unit) {
        // we add the item's ID to the "inventory" array of the user
        database.collection("users").document(user.getId()).update("inventory", FieldValue.arrayUnion(shopItem.id)).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(Result.GeneralSuccess(true))
            } else {
                callback(Result.Error(Exception("Error buying item")))
            }
        }
    }
}