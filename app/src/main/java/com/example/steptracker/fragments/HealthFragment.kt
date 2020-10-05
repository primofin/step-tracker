package com.example.steptracker.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.steptracker.objects.InternalFileStorageManager.dataFile
import com.example.steptracker.R
import kotlinx.android.synthetic.main.fragment_health.*


class HealthFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_health, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        readDataFromFile()
    }

    private fun readDataFromFile() {
        val dataFileList = mutableListOf<String>()
        requireActivity().openFileOutput(dataFile, Context.MODE_APPEND).use {
        }
        requireActivity().openFileInput(dataFile)?.bufferedReader()
            ?.useLines { lines ->
                lines.forEach {
                    dataFileList.add(
                        it
                    )
                }
                if (!dataFileList.isNullOrEmpty()) {
                    remind_txt.text = resources.getString(R.string.instruction);
                    weightValue.text = dataFileList[0]
                    heightValue.text = dataFileList[1]
                } else {
                    remind_txt.text = resources.getString(R.string.remind);
                }
            }
        calculateBMI()
    }

    private fun calculateBMI() {
        if (weightValue.text.isNullOrEmpty() || heightValue.text.isNullOrEmpty()) {
            return
        }
        val weight = weightValue.text.toString().toFloat()
        val height = heightValue.text.toString().toFloat()
        val bmi = (weight / (height * height / 10000))
        val bmiStr = ("%.2f".format(bmi))
        bmiValue.text = bmiStr
    }

    /**
     * @param: bmi - bmi value
     * This func return bmi interpretation based on the bmi value
     */
    private fun bmiRes(bmi: Float): String {
        var result = ""
        if (bmi < 18.5) {
            result = "Underweight"
        } else if (bmi > 18.5 && bmi < 24.9) {
            result = "Normal"
        } else if (bmi > 25 && bmi < 29.9) {
            result = "Overweight"
        } else {
            result = "Obese"
        }
        return result
    }
}