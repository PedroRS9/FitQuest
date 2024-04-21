package es.ulpgc.pigs.fitquest.data

interface ImageRepository {
    fun uploadImage(filename: String, byteArray: ByteArray, callback: (Result) -> Unit)
    fun downloadImage(pictureURL: String, callback: (Result) -> Unit)
}