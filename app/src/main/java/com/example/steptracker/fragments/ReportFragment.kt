package com.example.steptracker.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.steptracker.R
import com.example.steptracker.objects.DataObject.dateMap
import com.example.steptracker.objects.DataObject.reportDateFileList
import com.example.steptracker.objects.DataObject.reportStepFileList
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
        var listMap = mutableListOf<DataPoint>()
        var listDate = mutableListOf<String>()
        //Check if there is any data from firebase
        if (!dateMap.isNullOrEmpty()) {

            var i = 0.0 as Double
            dateMap.forEach { (k, v) ->
                listMap.add(DataPoint(i, v.toDouble()))
                listDate.add(LocalDate.parse(k).dayOfMonth.toString() + "-" + LocalDate.parse(k).monthValue.toString())
                i += 0.5
            }
        } else {
            //Take data from local file if there is no date from firebase
            var i = 0.0 as Double
            reportStepFileList.forEach {
                listMap.add(DataPoint(i, it.toDouble()))
                i += 0.5
            }
            reportDateFileList.forEach {
                listDate.add(LocalDate.parse(it).dayOfMonth.toString() + "-" + LocalDate.parse(it).monthValue.toString())
            }
        }
        //Check if there is any data in local storage
        if (listDate.isNullOrEmpty()) {
            //Empty graph
            graphView.gridLabelRenderer.isVerticalLabelsVisible = false
            graphView.gridLabelRenderer.isHorizontalLabelsVisible = false
            return v
        }
        //Build graph
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

}