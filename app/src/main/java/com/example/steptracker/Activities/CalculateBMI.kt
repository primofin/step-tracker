package com.example.steptracker.Activities

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.steptracker.R
import kotlinx.android.synthetic.main.activity_calculate_b_m_i.*
import android.view.View



class CalculateBMI : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculate_b_m_i)
        btnCalculate.setOnClickListener{
            calculateBMI()
        }
    }
    fun calculateBMI() {
        var weight = weightValue.text.toString().toFloat()
        var height = heightValue.text.toString().toFloat()
        bmiValue.text = (weight/(height*height)).toString()
    }
}