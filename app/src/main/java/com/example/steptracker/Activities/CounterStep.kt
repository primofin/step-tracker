package com.example.steptracker.Activities

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.steptracker.Listener.StepListener
import com.example.steptracker.R
import com.example.steptracker.sensorsHandler.StepDetector
import kotlinx.android.synthetic.main.activity_counter_step.*

class CounterStep : AppCompatActivity(), SensorEventListener, StepListener {
    private var simpleStepDetector: StepDetector? = null
    private var sensorManager: SensorManager? = null
    private val TEXT_NUM_STEPS = "Number of Steps: "
    private var numSteps: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_counter_step)

        // Get an instance of the SensorManager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        simpleStepDetector = StepDetector()
        simpleStepDetector!!.registerListener(this)

        btnStart.setOnClickListener(View.OnClickListener {
            numSteps = 0
            sensorManager!!.registerListener(this, sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST)
        })

        btnStop.setOnClickListener(View.OnClickListener {
            sensorManager!!.unregisterListener(this)
        })
    }
    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }
    override fun onSensorChanged(event: SensorEvent?) {
        if (event!!.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector!!.updateAccelerometer(event.timestamp, event.values[0], event.values[1], event.values[2])
        }
    }
    override fun step(timeNs: Long) {
        numSteps++
        stepValue.text = TEXT_NUM_STEPS.plus(numSteps)
    }

}