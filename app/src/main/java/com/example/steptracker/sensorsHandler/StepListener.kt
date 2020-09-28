package com.example.steptracker.sensorsHandler

interface StepListener {
    fun step(timeNs: Long)
}