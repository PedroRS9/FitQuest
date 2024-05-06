package es.ulpgc.pigs.fitquest.screens.mainmenu

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.ulpgc.pigs.fitquest.data.AchievementRepository
import es.ulpgc.pigs.fitquest.data.FirebaseAchievementRepository
import es.ulpgc.pigs.fitquest.data.FirebaseUserRepository
import es.ulpgc.pigs.fitquest.data.Result
import es.ulpgc.pigs.fitquest.data.User
import es.ulpgc.pigs.fitquest.data.UserRepository
import es.ulpgc.pigs.fitquest.global.EnteredApplicationAchievementId
import es.ulpgc.pigs.fitquest.global.FirstGoalCompletedAchievementId
import es.ulpgc.pigs.fitquest.global.UserGlobalConf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainMenuViewModel() : ViewModel() {
    private val userRepository: UserRepository = FirebaseUserRepository()
    private val achievementRepository: AchievementRepository = FirebaseAchievementRepository()

    private lateinit var userGlobalConf: UserGlobalConf

    private val _steps = MutableLiveData(0)
    val steps: LiveData<Int> = _steps

    private val _showStepGoalDialog = MutableLiveData(false)
    val showStepGoalDialog: LiveData<Boolean> = _showStepGoalDialog

    private val _achievementState = MutableLiveData<Result>()
    val achievementState: LiveData<Result> = _achievementState

    private val _playSoundState = MutableLiveData(false)
    val playSoundState: LiveData<Boolean> = _playSoundState

    private var initialSteps = -1
    private lateinit var user: User

    fun setUser(user: User) {
        this.user = user
    }

    fun initSensor(context: Context) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        val userInitialSteps = user.getSteps() // Steps which were previously stored in the database

        val sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val totalSteps = event.values[0].toInt()
                if (initialSteps == -1) {
                    initialSteps = totalSteps - userInitialSteps
                }
                val sessionSteps = totalSteps - initialSteps
                _steps.value = sessionSteps
                user.setSteps(sessionSteps)
                user.addXp(1)
                user.addPoints(1)
                if(user.getStepGoal() != 0 && sessionSteps >= user.getStepGoal() && !user.getAchievements().contains(FirstGoalCompletedAchievementId)){
                    user.addAchievement(FirstGoalCompletedAchievementId)
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            achievementRepository.getAchievement(FirstGoalCompletedAchievementId){ achievement ->
                                _playSoundState.postValue(true)
                                _achievementState.postValue(Result.AchievementSuccess(listOf(achievement)))
                            }
                        } catch (e: Exception) {
                            // Manejar excepción
                            val exceptionString = e.toString()
                        }
                    }
                }
                updateUser(user)
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        sensorManager.registerListener(sensorEventListener, stepSensor, SensorManager.SENSOR_DELAY_UI)
    }

    fun resetSteps() {
        _steps.value = 0
        initialSteps = -1
    }

    fun setUserGlobalConf(userGlobalConf: UserGlobalConf){
        this.userGlobalConf = userGlobalConf
    }

    fun checkStepGoal(user: User?) {
        if (user?.getStepGoal() == 0) {
            _showStepGoalDialog.value = true
        }
    }

    fun setStepGoal(user: User, stepGoal: Int) {
        user.setStepGoal(stepGoal)
        _showStepGoalDialog.value = false
        updateUser(user)
    }

    fun updateUser(user: User){
        userRepository.updateUser(user){ result: Result ->
            // do something
        }
    }

    fun requestStepGoalChange() {
        _showStepGoalDialog.value = true
    }

    suspend fun checkEnteredApplicationAchievement() {
        if (!user.getAchievements().contains(EnteredApplicationAchievementId)) {
            user.addAchievement(EnteredApplicationAchievementId)
            userRepository.updateUser(user) { result: Result ->
                if (result is Result.GeneralSuccess) {
                    // Lanzar una coroutine dentro del scope de la función suspendida
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            achievementRepository.getAchievement(EnteredApplicationAchievementId){ achievement ->
                                _playSoundState.postValue(true)
                                _achievementState.postValue(Result.AchievementSuccess(listOf(achievement)))
                            }
                        } catch (e: Exception) {
                            // Manejar excepción
                        }
                    }
                }
            }
        }
    }

    fun clearAchievementState(){
        _achievementState.value = null
    }

    fun clearPlaySoundState() {
        _playSoundState.value = false
    }
}