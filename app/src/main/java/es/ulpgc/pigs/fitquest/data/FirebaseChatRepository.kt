package es.ulpgc.pigs.fitquest.data

import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.FieldValue


class FirebaseChatRepository : ChatRepository {
    private val database = Firebase.firestore
    override fun getChatId(doctorId: String, userId: String, callback: (String?) -> Unit) {
        database.collection("chats")
            .whereEqualTo("doctorId", doctorId)
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    callback(result.documents[0].id)
                } else {
                    // if it doesn't exist, create a new chat
                    val chat = hashMapOf(
                        "doctorId" to doctorId,
                        "userId" to userId
                    )
                    database.collection("chats")
                        .add(chat)
                        .addOnSuccessListener { documentReference ->
                            callback(documentReference.id)
                        }
                        .addOnFailureListener { e ->
                            callback(null)
                        }
                }
            }
    }

    override fun getMessages(chatId: String, callback: (Result) -> Unit) {
        database.collection("chats").document(chatId).collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    callback(Result.Error(e))
                    return@addSnapshotListener
                }
                val messages = snapshot?.documents?.mapNotNull { doc ->
                    // we convert each document to a Message object manually
                    val content = doc.getString("content") ?: ""
                    val senderUsername = doc.getString("senderUsername") ?: ""
                    val timestamp = doc.getDate("timestamp") ?: return@mapNotNull null
                    Message(content, senderUsername, timestamp)
                } ?: listOf()
                callback(Result.ChatSuccess(messages))
            }
    }

    override fun sendMessage(
        chatId: String,
        senderUsername: String,
        message: Message,
        callback: (Result) -> Unit
    ) {
        val messageMap = hashMapOf(
            "content" to message.content,
            "senderUsername" to message.senderUsername,
            "timestamp" to FieldValue.serverTimestamp()
        )

        database.collection("chats").document(chatId).collection("messages")
            .add(messageMap)
            .addOnFailureListener { e ->
                callback(Result.Error(e))
            }
    }
}