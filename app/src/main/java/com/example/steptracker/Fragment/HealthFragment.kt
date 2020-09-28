package com.example.steptracker.Fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.steptracker.Object.InternalFileStorageManager
import com.example.steptracker.R
import kotlinx.android.synthetic.main.fragment_health.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HealthFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HealthFragment : Fragment() {


    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_health, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d("health","chay lai cai read")
        readDataFromFile()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HealthFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HealthFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
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
                weightValue.text = dataFileList[0]
                heightValue.text = dataFileList[1]
                Log.d("health","?? :D ????? ${dataFileList[0]}")
            }
        }
        Log.d("health","weight ${weightValue.text} height ${heightValue.text}")
        calculateBMI()
    }
    private fun calculateBMI() {
        if(weightValue.text.isNullOrEmpty() || heightValue.text.isNullOrEmpty()){
            return
        }
        var weight = weightValue.text.toString().toFloat()
        var height = heightValue.text.toString().toFloat()
        bmiValue.text = (weight/(height*height)).toString()
    }

}