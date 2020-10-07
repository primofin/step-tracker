package com.example.steptracker.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.steptracker.BmiActivity
import com.example.steptracker.objects.InternalFileStorageManager.dataFile
import com.example.steptracker.R
import com.example.steptracker.objects.DataObject.isLogged
import com.example.steptracker.objects.DataObject.userHeight
import com.example.steptracker.objects.DataObject.userWeight
import kotlinx.android.synthetic.main.fragment_health.*


class HealthFragment : Fragment() {
    var bmiInterpret = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_health, container, false)
        // Inflate the layout for this fragment
        val imgResult = view.findViewById(R.id.imgResult) as ImageView
        imgResult.setOnClickListener {
            val intent = Intent(activity, BmiActivity::class.java)
            intent.putExtra("bmiTxt", bmiInterpret)
            // start activity intent
            activity?.startActivity(intent)
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        readDataFromFile()
    }

    private fun readDataFromFile() {
        if (!isLogged) {    //if logged, use firebase data
            val dataFileList = mutableListOf<String>()
            //Read data to a list
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

        if (weightValue.text.isNullOrEmpty() || heightValue.text.isNullOrEmpty()) {
            remind_txt.text = resources.getString(R.string.remind);
            return
        }
        remind_txt.text = resources.getString(R.string.instruction)
        val weight = weightValue.text.toString().toFloat()
        val height = heightValue.text.toString().toFloat()
        val bmi = (weight / (height * height / 10000))
        val bmiStr = ("%.2f".format(bmi))
        bmiValue.text = bmiStr
        bmiRes(bmi)
    }

    /**
     * @param: bmi - bmi value
     * This func return bmi interpretation based on the bmi value
     */
    private fun bmiRes(bmi: Float): String {
        bmiInterpret = if (bmi < 18.5) {
            "Underweight"
        } else if (bmi > 18.5 && bmi < 24.9) {
            "Normal"
        } else if (bmi > 25 && bmi < 29.9) {
            "Overweight"
        } else {
            "Obese"
        }
        return bmiInterpret
    }
}