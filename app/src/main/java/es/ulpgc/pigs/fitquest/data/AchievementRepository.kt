package es.ulpgc.pigs.fitquest.data

interface AchievementRepository {
    suspend fun getAchievements(callback: (List<Achievement>) -> Unit)
    suspend fun getUserAchievements(user: User, callback: (List<Achievement>) -> Unit)
}