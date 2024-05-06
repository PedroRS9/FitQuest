package es.ulpgc.pigs.fitquest.data

import kotlin.math.pow

class User(
    private val id: String = "",
    private val name: String,
    private val password: String,
    private val email: String,
    private val isDoctor: Boolean,
    private var pictureURL: String? = null,
    // we show the default picture R.drawable.default_profile_pic if the user has not uploaded one
    private var picture: ByteArray? = null,
    private var level: Int = 1,
    private var xp: Int = 0,
    private var points: Int = 0,
    private var achievements: List<String> = listOf(),
    private var steps: Int = 0,
    private var stepGoal: Int = 0
) {
    fun getId() = id
    fun getName() = name
    fun getPassword() = password
    fun getEmail() = email
    fun getPictureURL() = pictureURL
    fun setPictureURL(pictureURL: String) {
        this.pictureURL = pictureURL
    }
    fun getPicture(): ByteArray? = picture
    fun setPicture(picture: ByteArray) {
        this.picture = picture
    }
    fun getLevel() = level

    fun getXp() = xp

    fun getSteps() = steps

    fun setSteps(steps: Int) {
        this.steps = steps
    }

    fun resetSteps() {
        this.steps = 0
    }

    fun getStepGoal() = stepGoal
    fun setStepGoal(stepGoal: Int) {
        this.stepGoal = stepGoal
    }

    fun setXp(xp: Int) {
        this.xp = xp
    }
    fun getPoints(): Int{
        return points
    }
    fun setPoints(points: Int){
        this.points = points
    }

    fun getAchievements(): List<String> = achievements
    fun addAchievement(achievementId: String) {
        achievements = achievements + achievementId
    }

    fun getMinimumXpForCurrentLevel(): Int{
        return ( (level-1) / 0.1).pow(2.0).toInt()
    }
    fun getXpToNextLevel(): Int{
        return (level / 0.1).pow(2.0).toInt()
    }

    fun calculateXpPercentage(): Float {
        val minXP = getMinimumXpForCurrentLevel()
        val maxXP = getXpToNextLevel()
        val userXp = getXp()
        if (userXp >= maxXP) return 1.0f // 100%
        if (userXp <= minXP) return 0.0f // 0%
        return (userXp - minXP).toFloat() / (maxXP - minXP).toFloat()
    }


    fun hasProfilePicture(): Boolean{
        return picture != null
    }

    fun isDoctor(): Boolean{
        return isDoctor
    }


    /**
     * Añade experiencia al usuario y devuelve si ha subido de nivel.
     *
     * @param xp La cantidad de experiencia a añadir.
     * @return true si ha subido de nivel, false en caso contrario.
     */
    fun addXp(xp: Int): Boolean{
        this.xp += xp
        if (this.xp >= getXpToNextLevel()){
            level++
            return true
        }
        return false
    }

    fun addSteps(steps: Int) {
        this.steps += steps
    }



    fun copy(): User {
        return User(id, name, password, email, isDoctor, pictureURL, picture, level, xp)
    }

}