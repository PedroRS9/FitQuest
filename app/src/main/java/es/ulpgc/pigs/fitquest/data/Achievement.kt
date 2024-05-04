package es.ulpgc.pigs.fitquest.data

data class Achievement(
    val id: String,
    val category: String,
    val title: String,
    val description: String,
    val image: ByteArray
)
