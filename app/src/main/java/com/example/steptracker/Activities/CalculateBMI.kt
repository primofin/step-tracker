package com.example.steptracker.Activities

import android.content.Context
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
    private fun calculateBMI() {
        if(weightValue.text.isNullOrEmpty() || heightValue.text.isNullOrEmpty()){
            return
        }
        var weight = weightValue.text.toString().toFloat()
        var height = heightValue.text.toString().toFloat()
        bmiValue.text = (weight/(height*height)).toString()
        writeDataToFile()
    }
    private fun writeDataToFile()
    {
        //mode private = rewrite the file. mode_append = add content to the file
        this!!.openFileOutput(dataFile, Context.MODE_PRIVATE).use {
            it.write("${weightValue.text}\n".toByteArray())
            it.write("${heightValue.text}\n".toByteArray())
        }
    }
    private fun readDataFromFile()
    {
        this!!.openFileInput(dataFile)?.bufferedReader()?.useLines{
                lines -> lines.forEach { dataFileList.add(it) }
            weightValue.setText(dataFileList[0])
            heightValue.setText(dataFileList[1])
            calculateBMI()
        }
    }
}