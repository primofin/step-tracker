package com.example.steptracker

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat.stopForeground
import androidx.core.content.ContextCompat
import android.os.Handler
import android.util.Log
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

class ForegroundService: Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


}