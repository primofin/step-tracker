package com.example.steptracker

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.steptracker.fragments.HealthFragment
import com.example.steptracker.fragments.MoreFragment
import com.example.steptracker.fragments.ReportFragment
import com.example.steptracker.fragments.TodayFragment
import com.example.steptracker.objects.DataObject.isLogged
import com.example.steptracker.objects.InternalFileStorageManager
import com.example.steptracker.objects.DataObject.isRunning
import com.example.steptracker.objects.DataObject.reportDateFileList
import com.example.steptracker.objects.DataObject.reportStepFileList
import com.example.steptracker.objects.DataObject.stepFileList
import com.example.steptracker.objects.DataObject.todayStep
import com.example.steptracker.objects.InternalFileStorageManager.dataFile
import com.example.steptracker.objects.InternalFileStorageManager.reportDateFile
import com.example.steptracker.objects.InternalFileStorageManager.reportStepFile
import com.example.steptracker.objects.InternalFileStorageManager.stepFile
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_today.*
import java.time.LocalDate


class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        todayStep = 0
        //avoid error when open file
        openFileOutput(dataFile, Context.MODE_APPEND).use {}
        openFileOutput(reportStepFile, Context.MODE_APPEND).use {}
        openFileOutput(reportDateFile, Context.MODE_APPEND).use {}
        openFileOutput(stepFile, Context.MODE_APPEND).use {}
        readDataFromFile()

        val todayFragment = TodayFragment()
        val reportFragment = ReportFragment()
        val healthFragment = HealthFragment()
        val moreFragment = MoreFragment()
        //Set the initial fragment to show
        if (savedInstanceState == null) {
            setCurrentFragment(moreFragment)    //run silent sign in
            setCurrentFragment(todayFragment)
        }

        //Set a listener that will be notified when a bottom navigation item is selected
        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.b1_today -> setCurrentFragment(todayFragment)
                R.id.b2_report -> setCurrentFragment(reportFragment)
                R.id.b3_health -> setCurrentFragment(healthFragment)
                R.id.b4_profile -> setCurrentFragment(moreFragment)
            }
            true
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    public override fun onResume() {
        super.onResume()
        if (isRunning)
            startBtn.performClick()
        readDataFromFile()
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.main_fragment, fragment)
            commit()
        }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun readDataFromFile() {
        reportDateFileList.clear()
        reportStepFileList.clear()
        openFileInput(reportStepFile)?.bufferedReader()
            ?.useLines { lines ->
                lines.forEach { reportStepFileList.add(it) }    //Get a record
            }
        println("step")
        println(reportStepFileList)
        openFileInput(reportDateFile)?.bufferedReader()
            ?.useLines { lines ->
                lines.forEach { reportDateFileList.add(it) }    //Get a record
            }
        println("date")
        println(reportDateFileList)

        openFileInput(stepFile)?.bufferedReader()?.useLines { lines ->
            lines.forEach { stepFileList.add(it) }
        } //Store data in file to a list

        if (!stepFileList.isNullOrEmpty()) {    //check if file is null or empty.
            val current = LocalDate.now()
            //Check if it is still a same day
            if (current.toString() == stepFileList[1]) {
                //Get today step
                todayStep = Integer.parseInt(stepFileList[0])
            } else {
                //init a new day record
                todayStep = 0

                openFileOutput(
                    reportStepFile,
                    Context.MODE_APPEND
                ).use {
                    it.write("${stepFileList[0]}\n".toByteArray())
                }
                openFileOutput(
                    reportDateFile,
                    Context.MODE_APPEND
                ).use {
                    it.write("${stepFileList[1]}\n".toByteArray())
                }
                openFileInput(reportStepFile)?.bufferedReader()
                    ?.useLines { lines ->
                        lines.forEach { reportStepFileList.add(it) }    //Get a record
                    }
                println("step")
                println(reportStepFileList)
                openFileInput(reportDateFile)?.bufferedReader()
                    ?.useLines { lines ->
                        lines.forEach { reportDateFileList.add(it) }    //Get a record
                    }
                println("date")
                println(reportDateFileList)
                //Check if it is already 7 days in record
                if (reportStepFileList.size > 7) {
                    reportStepFileList.remove(reportStepFileList.first())
                    openFileOutput(reportStepFile, Context.MODE_PRIVATE).use {
                        for (i in 0 until reportStepFileList.size) {
                            it.write("${reportStepFileList[i]}\n".toByteArray())
                        }
                    }
                }
                if (reportDateFileList.size > 7) {
                    reportDateFileList.remove(reportDateFileList.first())
                    openFileOutput(reportDateFile, Context.MODE_PRIVATE).use {
                        for (i in 0 until reportDateFileList.size) {
                            it.write("${reportDateFileList[i]}\n".toByteArray())
                        }
                    }
                }
                stepFileList[1] = current.toString()
            }
        } else {
            stepFileList.add(todayStep.toString())
            stepFileList.add(LocalDate.now().toString())
        }
    }

}



