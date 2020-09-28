package com.example.steptracker.Fragment

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.steptracker.Object.InternalFileStorageManager
import com.example.steptracker.Object.InternalFileStorageManager.dataFile
import com.example.steptracker.R
import com.example.steptracker.sensorsHandler.StepDetector
import kotlinx.android.synthetic.main.fragment_health.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_today.*

private val genders = arrayOf("Male", "Female")
class MoreFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_profile, container, false)
        val spinner = v.findViewById<Spinner>(R.id.sp_gender)
        spinner?.adapter = this.context?.let {
            ArrayAdapter(
                it,
                R.layout.support_simple_spinner_dropdown_item,
                genders
            )
        } as SpinnerAdapter
        spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
            }
        }
        return v
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // set default value to edit text views
        readDataFromFile()
        submitDataBtn.setOnClickListener{
                Toast.makeText(context, "Your information is saved !", Toast.LENGTH_SHORT).show()
                writeDataToFile(
                    et_user_weight.text.toString().toFloat(),
                    et_user_height.text.toString().toFloat()
                )
        }
    }
    private fun writeDataToFile(weight: Float, height: Float)
    {
        Log.d("write file","write file")
        //mode private = rewrite the file. mode_append = add content to the file
        activity!!.openFileOutput(dataFile, Context.MODE_PRIVATE).use {
            it.write("${weight}\n".toByteArray())
            it.write("${height}\n".toByteArray())
        }
    }
    // read data from file and set default value to edit text views
    private fun readDataFromFile()
    {
        var dataFileList = mutableListOf<String>()
        Log.d("health","read file")
        activity!!.openFileOutput(InternalFileStorageManager.dataFile, Context.MODE_APPEND).use {
            it.write("a line test".toByteArray())
        }
        activity!!.openFileInput(InternalFileStorageManager.dataFile)?.bufferedReader()?.useLines{ lines -> lines.forEach { dataFileList.add(
            it
        ) }
            if (dataFileList.size >1){
                et_user_weight.setText(dataFileList[0])
                et_user_height.setText(dataFileList[1])
            }
        }
    }
}