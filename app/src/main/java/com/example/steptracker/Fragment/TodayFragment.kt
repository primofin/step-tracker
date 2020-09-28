package com.example.steptracker.Fragment

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import com.example.steptracker.Objects.InternalFileStorageManager.stepFile
import com.example.steptracker.R
import com.example.steptracker.sensorsHandler.StepDetector
import com.example.steptracker.sensorsHandler.StepListener
import kotlinx.android.synthetic.main.fragment_today.*


class TodayFragment : Fragment(), SensorEventListener, StepListener {

    private var simpleStepDetector: StepDetector? = null
    private var sensorManager: SensorManager? = null
    private val TEXT_NUM_STEPS = "Number of Steps: "
    private var numSteps: Int = 0
    var stepFileList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //readDataFromFile()
        //circleTv.text = numSteps.toString()
        /*/ Get an instance of the SensorManager
        sensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        simpleStepDetector = StepDetector()
        simpleStepDetector!!.registerListener(this)

        startBtn.setOnClickListener(View.OnClickListener {
            sensorManager!!.registerListener(this, sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST)
    })
        stopBtn.setOnClickListener(View.OnClickListener {
            sensorManager!!.unregisterListener(this)
        })*/
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        readDataFromFile()
        circleTv.text = numSteps.toString()
        //
        //circleTv.text = numSteps.toString()
        // Get an instance of the SensorManager
        sensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        simpleStepDetector = StepDetector()
        simpleStepDetector!!.registerListener(this)

        startBtn.setOnClickListener(View.OnClickListener {
            sensorManager!!.registerListener(this, sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST)
    })
        stopBtn.setOnClickListener(View.OnClickListener {
            sensorManager!!.unregisterListener(this)
        })
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_today, container, false)
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
        circleTv.text = numSteps.toString()
        writeDataToFile()
    }
    private fun writeDataToFile()
    {
        //mode private = rewrite the file. mode_append = add content to the file
        activity!!.openFileOutput(stepFile, Context.MODE_PRIVATE).use {
            it.write("$numSteps\n".toByteArray())
        }
    }
    private fun readDataFromFile()
    {
        activity!!.openFileOutput(stepFile, Context.MODE_APPEND).use {
            it.write("a line test".toByteArray())
        }

        activity!!.openFileInput(stepFile)?.bufferedReader()?.useLines{
                lines -> lines.forEach { stepFileList.add(it) }
            if (stepFileList.size >1){
                numSteps= Integer.parseInt(stepFileList[0])
            }
        }
    }


}