package com.example.steptracker

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.steptracker.Fragment.HealthFragment
import com.example.steptracker.Fragment.MoreFragment
import com.example.steptracker.Fragment.ReportFragment
import com.example.steptracker.Fragment.TodayFragment
import com.example.steptracker.Object.InternalFileStorageManager
import com.example.steptracker.Object.fbObject.dbReference
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    //var database = FirebaseDatabase.getInstance()
    //var myRef = database.getReference("message")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        openFileOutput(InternalFileStorageManager.dataFile, Context.MODE_APPEND).use {
        }
        openFileOutput(InternalFileStorageManager.reportStepFile, Context.MODE_APPEND).use {

        }
        openFileOutput(InternalFileStorageManager.reportDateFile, Context.MODE_APPEND).use {

        }
        openFileOutput(InternalFileStorageManager.stepFile, Context.MODE_APPEND).use {
        }   //avoid error when open file


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
    public override fun  onStart(){
        super.onStart()

    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.main_fragment, fragment)
            commit()
        }
    private fun writeSthToFirebase()
    {
        //myRef.setValue("Hello, World!")

    }
}


