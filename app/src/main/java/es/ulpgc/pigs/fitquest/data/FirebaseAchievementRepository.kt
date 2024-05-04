package es.ulpgc.pigs.fitquest.data

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

class FirebaseAchievementRepository : AchievementRepository {
    private val database = Firebase.firestore
    val storageReference = Firebase.storage.reference

    override suspend fun getAchievements(callback: (List<Achievement>) -> Unit) {
        coroutineScope {
            val result = database.collection("achievements").get().await()
            val items = result.documents.mapNotNull { doc ->
                async {
                    val title = doc.getString("title") ?: ""
                    val description = doc.getString("description") ?: ""
                    val pictureURL = doc.getString("pictureURL") ?: ""
                    val category = doc.getString("category") ?: ""
                    val imageRef = storageReference.child(pictureURL)
                    val ONE_MEGABYTE: Long = 1024 * 1024
                    try {
                        val bytes = imageRef.getBytes(ONE_MEGABYTE).await()
                        Achievement(id = doc.id, title = title, description = description, category = category, image = bytes)
                    } catch (e: Exception) {
                        null // In case of error in the image download, decide how to handle it.
                    }
                }
            }.awaitAll().filterNotNull()
            callback(items)
        }
    }

    override suspend fun getUserAchievements(user: User, callback: (List<Achievement>) -> Unit) = coroutineScope {
        val achievementIds = user.getAchievements()
        val achievementFetchJobs = achievementIds.map { achievementId ->
            async {
                try {
                    val achievementDoc = database.collection("achievements").document(achievementId).get().await()
                    val title = achievementDoc.getString("title") ?: ""
                    val description = achievementDoc.getString("description") ?: ""
                    val pictureURL = achievementDoc.getString("pictureURL") ?: ""
                    val category = achievementDoc.getString("category") ?: ""
                    val imageRef = storageReference.child(pictureURL)
                    val ONE_MEGABYTE: Long = 1024 * 1024
                    try {
                        val bytes = imageRef.getBytes(ONE_MEGABYTE).await()
                        Achievement(id = achievementDoc.id, title = title, description = description, category = category, image = bytes)
                    } catch (e: Exception) {
                        null // In case of error in the image download, decide how to handle it.
                    }
                } catch (e: Exception) {
                    null // In case of error fetching achievement details
                }
            }
        }
        val achievements = achievementFetchJobs.awaitAll().filterNotNull()
        callback(achievements)
    }
}