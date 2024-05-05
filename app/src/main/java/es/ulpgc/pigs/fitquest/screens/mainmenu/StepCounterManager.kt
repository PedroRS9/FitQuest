import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.Toast

class StepCounterManager(private val context: Context) : SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var sensor: Sensor? = null
    private var running = false
    private var count = 0f

    fun initialize() {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        val sensor1 = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        if (sensor1 == null)
            Toast.makeText(context, "DETECTOR NOT FOUND", Toast.LENGTH_SHORT).show()
    }

    fun start() {
        running = true
        sensor?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_FASTEST)
        } ?: run {
            Toast.makeText(context, "SENSOR NOT FOUND", Toast.LENGTH_SHORT).show()
        }
    }

    fun stop() {
        running = false
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(sensorEvent: SensorEvent) {
        count = sensorEvent.values[0]
        // Puedes realizar acciones adicionales aquí si es necesario
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Sin operación
    }
}
