package com.example.steptracker.objects

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.database.*
import kotlin.properties.Delegates

object DataObject {

    var database = FirebaseDatabase.getInstance()
    var dbReference = database.getReference("users")
    lateinit var account: GoogleSignInAccount
    var isLogged: Boolean = false
    var isRunning: Boolean = false
    lateinit var userInfo: String
    var todayStep by Delegates.notNull<Int>()
    var stepFileList = mutableListOf<String>()
    var reportStepFileList = mutableListOf<String>()

}