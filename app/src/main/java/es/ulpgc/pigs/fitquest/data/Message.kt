package es.ulpgc.pigs.fitquest.data

import java.util.Date

data class Message(
    val content: String,
    val senderUsername: String,
    var timestamp: Date
){
    fun itsMyMessage(username: String): Boolean {
        return senderUsername == username
    }
}
