package com.example.steptracker

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat.stopForeground
import androidx.core.content.ContextCompat
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.steptracker.Object.InternalFileStorageManager
import com.example.steptracker.Object.InternalFileStorageManager.stepFile
import com.example.steptracker.Object.fbObject
import com.example.steptracker.Object.fbObject.todayStep
import com.example.steptracker.sensorsHandler.StepDetector
import com.example.steptracker.sensorsHandler.StepListener
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class ForegroundService: Service(), StepListener, SensorEventListener {
    private var simpleStepDetector: StepDetector? = null
    private var sensorManager: SensorManager? = null
    private val CHANNEL_ID = "ForegroundService Kotlin"
    companion object {
        fun startService(context: Context, message: String) {
            val startIntent = Intent(context, ForegroundService::class.java)
            startIntent.putExtra("inputExtra", message)
            ContextCompat.startForegroundService(context, startIntent)
        }
        fun stopService(context: Context) {
            val stopIntent = Intent(context, ForegroundService::class.java)
            context.stopService(stopIntent)
        }
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
            .setContentTitle("Foreground Service Kotlin Example")
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
        //stopSelf();
        return START_NOT_STICKY
    }
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun step(timeNs: Long) {
        writeDataToFile()
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(CHANNEL_ID, "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun writeDataToFile() {
        println("write ne write ne")
        println(todayStep)
        val current = LocalDate.now()
        //mode private = rewrite the file. mode_append = add content to the file
        openFileOutput(stepFile, Context.MODE_PRIVATE).use {
            it.write("$todayStep\n".toByteArray())
            println("du lieu file chay ngam  $todayStep ")
            it.write("$current\n".toByteArray())    //also write day to compare
        }
        if (fbObject.isLogged) {
            fbObject.dbReference.child(fbObject.account.id.toString()).child("Today Step").setValue(todayStep)
            fbObject.dbReference.child(fbObject.account.id.toString()).child("Today").setValue(current.toString())
            fbObject.dbReference.child(fbObject.account.id.toString()).child("Daily report").child(current.toString()).setValue(todayStep)

        }
    }

}