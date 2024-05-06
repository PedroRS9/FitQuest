package es.ulpgc.pigs.fitquest.screens.mainmenu

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.ulpgc.pigs.fitquest.data.FirebaseUserRepository
import es.ulpgc.pigs.fitquest.data.Result
import es.ulpgc.pigs.fitquest.data.User
import es.ulpgc.pigs.fitquest.data.UserRepository
import es.ulpgc.pigs.fitquest.global.UserGlobalConf

class MainMenuViewModel() : ViewModel() {
    private val userRepository: UserRepository = FirebaseUserRepository()

    private lateinit var userGlobalConf: UserGlobalConf

    private val _steps = MutableLiveData(0)
    val steps: LiveData<Int> = _steps

    private val _showStepGoalDialog = MutableLiveData(false)
    val showStepGoalDialog: LiveData<Boolean> = _showStepGoalDialog

    private var initialSteps = -1

    fun initSensor(context: Context) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        val sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val totalSteps = event.values[0].toInt()
                if (initialSteps == -1) {
                    initialSteps = totalSteps
                }
                val sessionSteps = totalSteps - initialSteps
                _steps.value = sessionSteps
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        sensorManager.registerListener(sensorEventListener, stepSensor, SensorManager.SENSOR_DELAY_UI)
    }

    fun resetSteps() {
        _steps.value = 0
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
}