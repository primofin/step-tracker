package com.example.steptracker

import android.app.*
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.annotation.RequiresApi
import com.example.steptracker.objects.InternalFileStorageManager.stepFile
import com.example.steptracker.objects.DataObject
import com.example.steptracker.objects.DataObject.dbReference
import com.example.steptracker.objects.DataObject.isLogged
import com.example.steptracker.objects.DataObject.todayStep
import com.example.steptracker.sensorsHandler.StepDetector
import com.example.steptracker.sensorsHandler.StepListener
import java.time.LocalDate

class ForegroundService : Service(), StepListener, SensorEventListener {
    private var simpleStepDetector: StepDetector? = null
    private var sensorManager: SensorManager? = null
    private val CHANNEL_ID = "ForegroundService"

    companion object {
        //Start service when this function get called
        fun startService(context: Context, message: String) {
            val startIntent = Intent(context, ForegroundService::class.java)
            startIntent.putExtra("inputExtra", message)
            ContextCompat.startForegroundService(context, startIntent)
        }

        //Stop service when this function get called
        fun stopService(context: Context) {
            val stopIntent = Intent(context, ForegroundService::class.java)
            context.stopService(stopIntent)
        }
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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //do heavy work on a background thread
        val input = intent?.getStringExtra("inputExtra")
        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Foreground Service")
            .setContentText(input)
            .setContentIntent(pendingIntent)
            .build()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        simpleStepDetector = StepDetector()
        simpleStepDetector!!.registerListener(this)
        sensorManager!!.registerListener(
            this,
            sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_FASTEST
        )
        startForeground(1, notification)
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun step(timeNs: Long) {
        todayStep++
        DataObject.stepFileList[0] = todayStep.toString()
        writeDataToFile()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID, "Foreground Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun writeDataToFile() {
        val current = LocalDate.now()
        //mode private = rewrite the file. mode_append = add content to the file
        openFileOutput(stepFile, Context.MODE_PRIVATE).use {
            it.write("$todayStep\n".toByteArray())
            it.write("$current\n".toByteArray())    //also write day to compare
        }
        //Check if the user logged in and push data to firebase
        if (isLogged) {
            dbReference.child(DataObject.account.id.toString()).child("Today Step")
                .setValue(todayStep)
            dbReference.child(DataObject.account.id.toString()).child("Today")
                .setValue(current.toString())
            dbReference.child(DataObject.account.id.toString()).child("Daily Report")
                .child(current.toString()).setValue(todayStep)
        }
    }

}