package com.example.steptracker.Fragment

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
import com.example.steptracker.Object.InternalFileStorageManager.stepFile
import com.example.steptracker.Object.fbObject
import com.example.steptracker.Object.fbObject.account
import com.example.steptracker.Object.fbObject.dbReference
import com.example.steptracker.Object.fbObject.isLogged
import com.example.steptracker.Object.fbObject.isRunning
import com.example.steptracker.Object.fbObject.todayStep
import com.example.steptracker.R
import com.example.steptracker.sensorsHandler.StepDetector
import com.example.steptracker.sensorsHandler.StepListener
import kotlinx.android.synthetic.main.fragment_today.*
import java.time.LocalDate
import kotlin.Unit.toString


class TodayFragment : Fragment(), SensorEventListener, StepListener {

    private var simpleStepDetector: StepDetector? = null
    private var sensorManager: SensorManager? = null
    var isOnScreen : Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        isOnScreen = true

        circleTv.text = todayStep.toString()
        println("mo lai cai activity nay")
        println(todayStep)

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
            isRunning= false
            ForegroundService.stopService(requireActivity())
        })
    }

    override fun onPause()
    {
        super.onPause()
        isOnScreen = false
    }
    override fun  onStart(){
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
        val mapBtn  = view.findViewById<Button>(R.id.mapBtn)
        // handle button click
        mapBtn.setOnClickListener {
            val intent = Intent(activity, MapActivity::class.java)
            // start activity intent
            activity?.startActivity(intent)
        }
        // Inflate the layout for this fragment
        return view
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

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

        todayStep++
        println(todayStep)
        fbObject.stepFileList[0] = todayStep.toString()
        if (isOnScreen)
            circleTv.text = todayStep.toString()
        //writeDataToFile()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun writeDataToFile() {

        val current = LocalDate.now()
        //mode private = rewrite the file. mode_append = add content to the file
        requireActivity().openFileOutput(stepFile, Context.MODE_PRIVATE).use {
            it.write("$todayStep\n".toByteArray())
            it.write("$current\n".toByteArray())    //also write day to compare
        }
        if (isLogged) {
            dbReference.child(account.id.toString()).child("Today Step").setValue(todayStep)
            dbReference.child(account.id.toString()).child("Today").setValue(current.toString())
            dbReference.child(account.id.toString()).child("Daily report").child(current.toString()).setValue(
                todayStep
            )

        }
    }

}