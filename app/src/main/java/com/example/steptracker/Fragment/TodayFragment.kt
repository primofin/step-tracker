package com.example.steptracker.Fragment

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.steptracker.MapActivity
import com.example.steptracker.Object.InternalFileStorageManager.reportDateFile
import com.example.steptracker.Object.InternalFileStorageManager.reportStepFile
import com.example.steptracker.Object.InternalFileStorageManager.stepFile
import com.example.steptracker.Object.fbObject
import com.example.steptracker.Object.fbObject.account
import com.example.steptracker.Object.fbObject.dbReference
import com.example.steptracker.Object.fbObject.isLogged
import com.example.steptracker.Object.fbObject.todayStep
import com.example.steptracker.R
import com.example.steptracker.sensorsHandler.StepDetector
import com.example.steptracker.sensorsHandler.StepListener
import kotlinx.android.synthetic.main.fragment_today.*
import java.time.LocalDate


class TodayFragment : Fragment(), SensorEventListener, StepListener {

    private var simpleStepDetector: StepDetector? = null
    private var sensorManager: SensorManager? = null
    private var numSteps: Int = 0
    private var stepFileList = mutableListOf<String>()
    private var reportStepFileList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        readDataFromFile()
        circleTv.text = numSteps.toString()
        sensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        simpleStepDetector = StepDetector()
        simpleStepDetector!!.registerListener(this)

        startBtn.setOnClickListener(View.OnClickListener {
            Toast.makeText(context, "Counter is started !", Toast.LENGTH_SHORT).show()
            counterState.text = ""
            sensorManager!!.registerListener(
                this,
                sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST
            )
        })
        pauseBtn.setOnClickListener(View.OnClickListener {
            Toast.makeText(context, "Counter is paused !", Toast.LENGTH_SHORT).show()
            counterState.text = getString(R.string.isPaused)
            sensorManager!!.unregisterListener(this)
        })
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

        numSteps++
        todayStep=numSteps
        circleTv.text = numSteps.toString()
        writeDataToFile()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun writeDataToFile() {

        val current = LocalDate.now()
        //mode private = rewrite the file. mode_append = add content to the file
        requireActivity().openFileOutput(stepFile, Context.MODE_PRIVATE).use {
            it.write("$numSteps\n".toByteArray())
            it.write("$current\n".toByteArray())    //also write day to compare
        }
        if (isLogged) {
            dbReference.child(account.id.toString()).child("Today Step").setValue(numSteps)
            dbReference.child(account.id.toString()).child("Today").setValue(current.toString())
            dbReference.child(account.id.toString()).child("Daily report").child(current.toString()).setValue(numSteps)

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun readDataFromFile() {


        requireActivity().openFileInput(stepFile)?.bufferedReader()?.useLines { lines ->
            lines.forEach { stepFileList.add(it) }  //Store data in file to a list
            if (!stepFileList.isNullOrEmpty()) {    //check if file is null or empty.
                val current = LocalDate.now()
                if (current.toString() == stepFileList[1])  //Check if it is still a same day
                    numSteps = Integer.parseInt(stepFileList[0])    //Get today step
                else {

                    numSteps = 0    //init a new day record
                    requireActivity().openFileOutput(reportStepFile, Context.MODE_APPEND).use {
                        it.write("${stepFileList[0]}\n".toByteArray())
                    }
                    requireActivity().openFileInput(reportStepFile)?.bufferedReader()?.useLines { lines ->
                        lines.forEach { reportStepFileList.add(it) }    //Get a record
                    }
                    if (reportStepFileList.size < 7) {  //Check if it is already 7 days in record
                        requireActivity().openFileOutput(reportStepFile, Context.MODE_PRIVATE).use {
                            //write again from the beginning due to the test
                            for (i in 0..reportStepFileList.size - 1) {
                                it.write("${reportStepFileList[i]}\n".toByteArray())
                            }
                        }
                        requireActivity().openFileOutput(reportDateFile, Context.MODE_APPEND).use {
                            //current.minusDays(1)
                            it.write("${current.minusDays(1)}\n".toByteArray())    // no need for date file because no test here
                        }
                    }
                }
            }
        }


    }
}