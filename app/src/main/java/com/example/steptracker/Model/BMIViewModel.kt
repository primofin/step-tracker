package com.example.steptracker.Model

import androidx.lifecycle.ViewModel

class BMIViewModel: ViewModel() {
    var liveWeight  = 0 as Float
    var liveHeight = 0 as Float
    var liveBMI  = 0 as Float
}