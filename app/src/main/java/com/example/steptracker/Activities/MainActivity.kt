package com.example.steptracker.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.steptracker.R

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
    fun toCounterActivity(view: View){
        val intent = Intent(this, CounterStep::class.java)
        startActivity(intent)
    }
    fun toBMIActivity(view: View){
        val intent = Intent(this, CalculateBMI::class.java)
        startActivity(intent)
    }

}