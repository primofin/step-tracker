package com.example.steptracker

import android.content.Context
import android.os.Build
import android.os.Bundle

import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.steptracker.Fragment.HealthFragment
import com.example.steptracker.Fragment.MoreFragment
import com.example.steptracker.Fragment.ReportFragment
import com.example.steptracker.Fragment.TodayFragment
import com.example.steptracker.Object.InternalFileStorageManager
import com.example.steptracker.Object.fbObject
import com.example.steptracker.Object.fbObject.isRunning
import com.example.steptracker.Object.fbObject.reportStepFileList
import com.example.steptracker.Object.fbObject.stepFileList
import com.example.steptracker.Object.fbObject.todayStep

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_today.*
import java.time.LocalDate



class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        todayStep= 0
        openFileOutput(InternalFileStorageManager.dataFile, Context.MODE_APPEND).use {
        }
        openFileOutput(InternalFileStorageManager.reportStepFile, Context.MODE_APPEND).use {

        }
        openFileOutput(InternalFileStorageManager.reportDateFile, Context.MODE_APPEND).use {

        }
        openFileOutput(InternalFileStorageManager.stepFile, Context.MODE_APPEND).use {
        }   //avoid error when open file
        readDataFromFile()


        val todayFragment = TodayFragment()
        val reportFragment = ReportFragment()
        val healthFragment = HealthFragment()
        val moreFragment = MoreFragment()
        //Set the initial fragment to show
        if (savedInstanceState == null) {
            setCurrentFragment(moreFragment)
            setCurrentFragment(todayFragment)
        }

        //Set a listener that will be notified when a bottom navigation item is selected
        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.b1_today -> setCurrentFragment(todayFragment)
                R.id.b2_report -> setCurrentFragment(reportFragment)
                R.id.b3_health -> {
                    setCurrentFragment(healthFragment)

                }
                R.id.b4_profile -> setCurrentFragment(moreFragment)
            }
            true
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    public override fun onStart() {
        super.onStart()
        if (isRunning)
            readDataFromFile()
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
        openFileInput(InternalFileStorageManager.stepFile)?.bufferedReader()?.useLines { lines ->
            lines.forEach {stepFileList.add(it) }  //Store data in file to a list
            if (!stepFileList.isNullOrEmpty()) {    //check if file is null or empty.
                val current = LocalDate.now()
                if (current.toString() == stepFileList[1]) {
                    //Check if it is still a same day
                    fbObject.todayStep = Integer.parseInt(stepFileList[0])    //Get today step
                    println("today step trong file ${fbObject.todayStep}")
                    println("du lieu trong file ${stepFileList[0]} ")
                }
                else {

                    fbObject.todayStep = 0    //init a new day record

                    openFileOutput(InternalFileStorageManager.reportStepFile, Context.MODE_APPEND).use {
                        it.write("${stepFileList[0]}\n".toByteArray())
                    }
                    openFileInput(InternalFileStorageManager.reportStepFile)?.bufferedReader()?.useLines { lines ->
                        lines.forEach { reportStepFileList.add(it) }    //Get a record
                    }
                    if (reportStepFileList.size < 7) {  //Check if it is already 7 days in record
                        openFileOutput(InternalFileStorageManager.reportStepFile, Context.MODE_PRIVATE).use {
                            //write again from the beginning due to the test
                            for (i in 0 until reportStepFileList.size) {
                                it.write("${reportStepFileList[i]}\n".toByteArray())
                            }
                        }
                        openFileOutput(InternalFileStorageManager.reportDateFile, Context.MODE_APPEND).use {
                            //current.minusDays(1)
                            it.write("${current.minusDays(1)}\n".toByteArray())    // no need for date file because no test here
                        }
                    }
                }
            }
        }

    }

}


