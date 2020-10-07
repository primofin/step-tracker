package com.example.steptracker.fragments

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.steptracker.R
import com.example.steptracker.objects.DataObject.dateMap
import com.example.steptracker.objects.DataObject.reportDateFileList
import com.example.steptracker.objects.DataObject.reportStepFileList
import com.example.steptracker.objects.InternalFileStorageManager
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.helper.StaticLabelsFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.time.LocalDate


class ReportFragment : Fragment() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_report, container, false)
        val graphView = v.findViewById<GraphView>(R.id.stepGraph)

        var i=0.0 as Double
        var listMap = mutableListOf<DataPoint>()
        var listDate = mutableListOf<String>()
        dateMap.forEach { (k, v) ->
            listMap.add(DataPoint(i, v.toDouble()))
            listDate.add(LocalDate.parse(k).dayOfMonth.toString()+"-"+LocalDate.parse(k).monthValue.toString())
            i += 0.5
        }


        graphView.addSeries(LineGraphSeries(listMap.toTypedArray()))
        graphView.viewport.isXAxisBoundsManual = true;
        graphView.gridLabelRenderer.setHumanRounding(true)
        graphView.viewport.isScrollable = true;
        graphView.gridLabelRenderer.padding = 60;
        val staticLabelsFormatter = StaticLabelsFormatter(graphView)
        staticLabelsFormatter.setHorizontalLabels(listDate.toTypedArray())
        graphView.gridLabelRenderer.labelFormatter = staticLabelsFormatter;

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
            }
    }
}