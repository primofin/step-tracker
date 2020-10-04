package com.example.steptracker.fragments

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.steptracker.objects.InternalFileStorageManager.dataFile
import com.example.steptracker.objects.DataObject.account
import com.example.steptracker.objects.DataObject.dbReference
import com.example.steptracker.objects.DataObject.isLogged
import com.example.steptracker.objects.DataObject.userInfo
import com.example.steptracker.R
import com.example.steptracker.objects.DataObject.todayStep
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_profile.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val genders = arrayOf("Male", "Female")

class MoreFragment : Fragment() {
    lateinit var mGoogleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_profile, container, false)
        val spinner = v.findViewById<Spinner>(R.id.sp_gender)
        spinner?.adapter = this.context?.let {
            ArrayAdapter(
                it,
                R.layout.support_simple_spinner_dropdown_item,
                genders
            )
        } as SpinnerAdapter
        spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
            }
        }
        return v
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //Set google button text
        if (isLogged)
            googleBtn.text = "Sign Out"

        // set default value to edit text views
        readDataFromFile()
        submitDataBtn.setOnClickListener {
            if (!et_user_weight.text.isNullOrEmpty() && !et_user_height.text.isNullOrEmpty()) {
                Toast.makeText(context, "Your information is saved !", Toast.LENGTH_SHORT).show()
                writeDataToFile(
                    et_user_weight.text.toString().toFloat(),
                    et_user_height.text.toString().toFloat(),
                    sp_gender.selectedItem.toString()
                )
            } else {
                Toast.makeText(context, "You should enter required information", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("534333492380-163qq0hl74sui67kgbinj0vjce2surbd.apps.googleusercontent.com")
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        //Check if already sign in last season. Stay signed in
        mGoogleSignInClient!!.silentSignIn()
            .addOnCompleteListener(
                requireActivity()
            ) { task -> handleSignInResult(task) }

        googleBtn.setOnClickListener {
            if (!isLogged) {
                googleBtn.text = "Sign Out"
                isLogged = true
                signIn()
            } else {
                isLogged = false
                googleBtn.text = "Restore"
                signOut()
            }
        }
    }

    private fun writeDataToFile(weight: Float, height: Float, gender: String) {
        //mode private = rewrite the file. mode_append = add content to the file
        requireActivity().openFileOutput(dataFile, Context.MODE_PRIVATE).use {
            it.write("${weight}\n".toByteArray())
            it.write("${height}\n".toByteArray())
            it.write("${gender}\n".toByteArray())

        }
        //Check if the use logged in, then push data to firebase
        if (isLogged) {
            dbReference.child(account.id.toString()).child("Weight").setValue(weight)
            dbReference.child(account.id.toString()).child("Height").setValue(height)
            dbReference.child(account.id.toString()).child("Gender").setValue(gender)

        }
    }

    // read data from file and set default value to edit text views
    private fun readDataFromFile() {
        var dataFileList = mutableListOf<String>()
        requireActivity().openFileInput(dataFile)?.bufferedReader()
            ?.useLines { lines ->
                lines.forEach {
                    dataFileList.add(
                        it
                    )
                }
                if (!dataFileList.isNullOrEmpty()) {
                    et_user_weight.setText(dataFileList[0])
                    et_user_height.setText(dataFileList[1])
                }
            }
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(
            signInIntent, RC_SIGN_IN
        )
    }

    private fun signOut() {
        isLogged = false
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(requireActivity()) {
                // Update your UI here
            }
    }

    //In case want to delete account
    private fun revokeAccess() {
        mGoogleSignInClient.revokeAccess()
            .addOnCompleteListener(requireActivity()) {
                // Update your UI here
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount?>) {
        try {
            account = completedTask.getResult(
                ApiException::class.java
            )!!

            // Signed in successfully
            isLogged = true
            val googleId = account?.id ?: ""
            Log.i("Google ID", googleId)

            val googleFirstName = account?.givenName ?: ""
            Log.i("Google First Name", googleFirstName)
            dbReference.child(googleId).child("First name").setValue(googleFirstName)

            val googleLastName = account?.familyName ?: ""
            Log.i("Google Last Name", googleLastName)
            dbReference.child(googleId).child("Last name").setValue(googleLastName)

            val googleEmail = account?.email ?: ""
            Log.i("Google Email", googleEmail)
            dbReference.child(googleId).child("Email").setValue(googleEmail)

            val googleProfilePicURL = account?.photoUrl.toString()
            Log.i("Google Profile Pic URL", googleProfilePicURL)
            dbReference.child(googleId).child("ProfilePictureUrl").setValue(googleProfilePicURL)

            val googleIdToken = account?.idToken ?: ""
            Log.i("Google ID Token", googleIdToken)
            dbReference.child(googleId).child("Today").setValue(LocalDate.now().toString())
            dbReference.child(googleId).child("Today Step").setValue(todayStep)
            dbReference.child(googleId).child("Daily Report").child(LocalDate.now().toString())
                .setValue(todayStep)

            //fetch data from Firebase
            val menuListener = object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {
                    // handle error
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val takenFireBaseData = dataSnapshot.value
                    userInfo = Gson().toJson(takenFireBaseData)
                }
            }
            (account.id?.let { it1 -> dbReference.child(it1) })?.addValueEventListener(menuListener)

        } catch (e: ApiException) {
            isLogged = false
            // Sign in was unsuccessful
            Log.e(
                "failed code=", e.statusCode.toString()
            )
        }
    }

}

