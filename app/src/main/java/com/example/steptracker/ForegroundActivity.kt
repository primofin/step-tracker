package com.example.steptracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat

class ForegroundActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_foreground_acitivity)
        ContextCompat.startForegroundService(this, intent)
    }
    private fun startForegroundService() {
        val intent = Intent(this, ForegroundService::class.java)
        startService(intent)
    }
}