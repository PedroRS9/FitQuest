package es.ulpgc.pigs.fitquest.data

sealed class SearchResult{
    object Loading: SearchResult()
    data class Results(val results: List<User>) : SearchResult()
    data class ShowError(val exception: Exception) : SearchResult()
}
