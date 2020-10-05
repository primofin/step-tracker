package com.example.steptracker.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.steptracker.R
import com.example.steptracker.objects.DataObject.reportDateFileList
import com.example.steptracker.objects.DataObject.reportStepFileList
import com.example.steptracker.objects.InternalFileStorageManager
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.text.SimpleDateFormat


class ReportFragment : Fragment() {
    val dataPoints = Array(reportDateFileList.size) {
        DataPoint(
            0.toDouble(),
            reportStepFileList[it].toDouble()
        )
    }
//        var series = LineGraphSeries(
//        arrayOf(
//            DataPoint(0.toDouble(), 1.toDouble()),
//            DataPoint(1.toDouble(), 5.toDouble()),
//            DataPoint(2.toDouble(), 3.toDouble()),
//            DataPoint(3.toDouble(), 2.toDouble()),
//            DataPoint(4.toDouble(), 6.toDouble())
//        )
//    )
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_report, container, false)
        val graphView = v.findViewById<GraphView>(R.id.stepGraph)
        // set date label formatter
        val graphFormatter = SimpleDateFormat("dd/MM")
        graphView.gridLabelRenderer.labelFormatter =
            DateAsXAxisLabelFormatter(context, graphFormatter)
        graphView.gridLabelRenderer.setHumanRounding(false)
        graphView.addSeries(LineGraphSeries(dataPoints))
//        graphView.addSeries(series)
        graphView.gridLabelRenderer.padding = 60;
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        readDataFromFile()
    }

    private fun readDataFromFile() {
        val reportDateFileList = mutableListOf<String>()
        requireActivity().openFileOutput(
            InternalFileStorageManager.reportDateFile,
            Context.MODE_APPEND
        )
            .use {
            }
        requireActivity().openFileInput(InternalFileStorageManager.reportDateFile)?.bufferedReader()
            ?.useLines { lines ->
                lines.forEach {
                    reportDateFileList.add(
                        it
                    )
                }
                if (!reportDateFileList.isNullOrEmpty()) {
//                    weightValue.text = dataFileList[0]
//                    heightValue.text = dataFileList[1]
                }
            }
    }
}