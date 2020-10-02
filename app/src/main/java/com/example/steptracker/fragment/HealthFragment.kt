package com.example.steptracker.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.steptracker.`object`.InternalFileStorageManager.dataFile
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
                    weightValue.text = dataFileList[0]
                    heightValue.text = dataFileList[1]
                }
            }
        calculateBMI()
    }

    private fun calculateBMI() {
        if (weightValue.text.isNullOrEmpty() || heightValue.text.isNullOrEmpty())
            return
        val weight = weightValue.text.toString().toFloat()
        val height = heightValue.text.toString().toFloat()
        bmiValue.text = (weight / (height * height)).toString()
    }

}