package com.example.steptracker.fragments

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.steptracker.ForegroundService
import com.example.steptracker.MapActivity
import com.example.steptracker.objects.DataObject.isRunning
import com.example.steptracker.objects.DataObject.todayStep
import com.example.steptracker.R
import com.example.steptracker.objects.DataObject.stepFileList
import com.example.steptracker.sensorsHandler.StepDetector
import com.example.steptracker.sensorsHandler.StepListener
import kotlinx.android.synthetic.main.fragment_today.*


class TodayFragment : Fragment(), SensorEventListener, StepListener {

    private var simpleStepDetector: StepDetector? = null
    private var sensorManager: SensorManager? = null
    private var isOnScreen: Boolean = true

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        isOnScreen = true
        circleTv.text = todayStep.toString()

        sensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        simpleStepDetector = StepDetector()
        simpleStepDetector!!.registerListener(this)

        startBtn.setOnClickListener(View.OnClickListener {
            if (!isRunning)
                Toast.makeText(context, "Counter is started !", Toast.LENGTH_SHORT).show()
            counterState.text = ""
            sensorManager!!.registerListener(
                this,
                sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST
            )
            isRunning = true
            ForegroundService.startService(requireActivity(), "Foreground Service is running...")
        })
        pauseBtn.setOnClickListener(View.OnClickListener {
            Toast.makeText(context, "Counter is paused !", Toast.LENGTH_SHORT).show()
            counterState.text = getString(R.string.isPaused)
            sensorManager!!.unregisterListener(this)
            isRunning = false
            ForegroundService.stopService(requireActivity())
        })
    }

    //Check if fragment is visible
    override fun onPause() {
        super.onPause()
        isOnScreen = false
    }

    override fun onStart() {
        super.onStart()
        isOnScreen = true
    }

    override fun onResume() {
        super.onResume()
        isOnScreen = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_today, container, false)
        // button
        val mapBtn = view.findViewById<Button>(R.id.mapBtn)
        // handle button click
        mapBtn.setOnClickListener {
            val intent = Intent(activity, MapActivity::class.java)
            // start activity intent
            activity?.startActivity(intent)
        }
        // Inflate the layout for this fragment
        return view
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}

    override fun onSensorChanged(event: SensorEvent?) {
        if (event!!.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector!!.updateAccelerometer(
                event.timestamp,
                event.values[0],
                event.values[1],
                event.values[2]
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun step(timeNs: Long) {
        //Only set text when fragment visible, else crash app
        if (isOnScreen)
            circleTv.text = stepFileList[0]
    }
}