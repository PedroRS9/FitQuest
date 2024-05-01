package es.ulpgc.pigs.fitquest.data

sealed class Result{
    data class GeneralSuccess(val boolean: Boolean): Result()
    data class Error(val exception: Exception): Result()
    object Loading: Result()
    data class LoginSuccess(val user: User): Result()
    data class ImageSuccess(val bytes: ByteArray): Result()
    data class ChatListSuccess(val users: List<User>): Result()
    data class ChatSuccess(val messages: List<Message>): Result()
}