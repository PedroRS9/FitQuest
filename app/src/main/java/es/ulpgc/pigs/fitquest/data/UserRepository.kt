package es.ulpgc.pigs.fitquest.data

interface UserRepository {
    fun createUser(user: User, callback: (Result) -> (Unit))
    fun findUserByUsername(username: String, callback: (User?) -> Unit)
    fun findUserByEmail(email: String, callback: (User?) -> Unit)
    fun updateUser(user: User, callback: (Result) -> Unit)
    fun searchUsers(query: String, callback: (SearchResult) -> Unit)
    fun getAllDoctors(callback: (List<User>) -> Unit)
}