package com.example.camera

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.atan2

class SensorHelper(context: Context) : SensorEventListener {

  private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
  private val accelerometer: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

  private val _rollDegrees = MutableStateFlow(0f)
  val rollDegrees: StateFlow<Float> = _rollDegrees.asStateFlow()

  fun start() {
    accelerometer?.let {
      sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
    }
  }

  fun stop() {
    sensorManager?.unregisterListener(this)
  }

  override fun onSensorChanged(event: SensorEvent?) {
    if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
      val x = event.values[0]
      val y = event.values[1]
      // Calculate roll angle in degrees
      val angle = Math.toDegrees(atan2(x.toDouble(), y.toDouble())).toFloat()
      _rollDegrees.value = angle
    }
  }

  override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    // No-op
  }
}
