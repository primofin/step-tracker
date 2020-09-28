package com.example.steptracker

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.steptracker.Fragment.HealthFragment
import com.example.steptracker.Fragment.MoreFragment
import com.example.steptracker.Fragment.ReportFragment
import com.example.steptracker.Fragment.TodayFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val todayFragment = TodayFragment()
        val reportFragment = ReportFragment()
        val healthFragment = HealthFragment()
        val moreFragment = MoreFragment()
        //Set the initial fragment to show
        if (savedInstanceState == null) {
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

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.main_fragment, fragment)
            commit()
        }
}

