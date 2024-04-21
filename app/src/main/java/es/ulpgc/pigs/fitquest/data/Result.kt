package es.ulpgc.pigs.fitquest.data

sealed class Result{
    data class GeneralSuccess(val boolean: Boolean): Result()
    data class Error(val exception: Exception): Result()
    object Loading: Result()
    data class LoginSuccess(val user: User): Result()
    data class ImageSuccess(val bytes: ByteArray): Result()

}