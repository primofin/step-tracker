package com.example.steptracker.Object

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.database.*
import org.json.JSONObject
import kotlin.properties.Delegates

object fbObject {

    var database = FirebaseDatabase.getInstance()
    var dbReference = database.getReference("users")
    lateinit var account: GoogleSignInAccount
    var isLogged: Boolean = false
    lateinit var userInfo : String
    var todayStep = 0
    var stepFileList = mutableListOf<String>()
    var reportStepFileList = mutableListOf<String>()

}