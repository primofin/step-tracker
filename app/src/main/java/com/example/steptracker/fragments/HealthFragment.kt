package com.example.steptracker.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.steptracker.objects.InternalFileStorageManager.dataFile
import com.example.steptracker.R
import com.example.steptracker.objects.DataObject.isLogged
import com.example.steptracker.objects.DataObject.userHeight
import com.example.steptracker.objects.DataObject.userWeight
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
        if(!isLogged) {
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
                        userWeight = dataFileList[0]
                        userHeight = dataFileList[1]
                    }
                }
        }
        weightValue.text = userWeight
        heightValue.text = userHeight
        calculateBMI()
    }

    private fun calculateBMI() {
        if (weightValue.text.isNullOrEmpty() || heightValue.text.isNullOrEmpty()){
            bmiValue.text = ""
        }
        else {
            val weight = weightValue.text.toString().toDouble()
            val height = heightValue.text.toString().toDouble()
            bmiValue.text = (weight / (height * height / 10000)).toString()
        }
    }
}