package es.ulpgc.pigs.fitquest.data

interface ChatRepository {

    fun getChatId(doctorId: String, userId: String, callback: (String?) -> Unit)
    fun getMessages(chatId: String, callback: (Result) -> Unit)
    fun sendMessage(chatId: String, senderUsername: String, message: Message, callback: (Result) -> Unit)
}