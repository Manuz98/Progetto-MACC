package com.example.mobileproject

import android.R.attr
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_register.*
import android.widget.*
import java.util.*
import com.google.android.gms.common.api.CommonStatusCodes

import com.google.android.gms.common.api.ApiException

import androidx.annotation.NonNull

import com.google.android.gms.tasks.OnFailureListener

import com.google.android.gms.safetynet.SafetyNetApi

import com.google.android.gms.tasks.OnSuccessListener

import com.google.android.gms.safetynet.SafetyNet
import com.google.android.gms.safetynet.SafetyNetApi.RecaptchaTokenResponse
import kotlinx.android.synthetic.main.activity_login.*
import android.app.Activity
import android.R.attr.password
import android.R.attr.y

import android.app.DatePickerDialog
import android.view.View
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.widget.DatePicker




class Register : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize Firebase Auth
        auth = Firebase.auth
        database = FirebaseDatabase.getInstance("https://project-macc-default-rtdb.europe-west1.firebasedatabase.app")

        reference = database.getReference("users")
        val datePicker = signupbirthdate
        val today = Calendar.getInstance()
        val text=signupmessage
        today.set(2020,11,31)
        datePicker.maxDate = today.getTimeInMillis()
        datePicker.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)

        ) { view, year, month, day ->
            val month = month + 1
            val msg = "You Selected: $day/$month/$year"
            text.setText("")
            text.append((msg))
        }


        signupbutton.setOnClickListener{
            if(checkForm()){

                signUpUser()
            }

        }


    }


    private fun checkForm(): Boolean {
        var flag = true
        if(signupname.text.toString() == ""){
            signupname.error = resources.getString(R.string.name_blank)
            flag = false
        }
        if(signupsurname.text.toString() == ""){
            signupsurname.error = resources.getString(R.string.surname_blank)
            flag = false
        }
        if(signupemail.text.toString() == ""){
            signupemail.error = resources.getString(R.string.email_blank)
            flag = false
        }
        else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(signupemail.text.toString()).matches()) {
            signupemail.requestFocus()
            signupemail.error = resources.getString(R.string.email_miss_error)
            flag = false
        }
        if(signuppassword.text.toString().isEmpty()){
            signuppassword.error = resources.getString(R.string.password_blank)
            flag = false
        }
        else if(signuppassword.text.toString().length < 6) {
            signuppassword.error = resources.getString(R.string.password_format_error)
            flag = false
        }
        if(!signuppasswordrepeat.text.toString().equals(signuppassword.text.toString())) {
            signuppasswordrepeat.error = resources.getString(R.string.password_confirmation_error)
            flag = false
        }
        return flag
    }




    private fun signUpUser(){
            auth.createUserWithEmailAndPassword(signupemail.text.toString(), signuppassword.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user: FirebaseUser? = auth.currentUser
                        user?.sendEmailVerification()
                        ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            var database = Database()
                            var name = signupname.text.toString().trim()
                            var surname=signupsurname.text.toString().trim()
                            var email = signupemail.text.toString().trim()
                            var birthday = signupbirthdate
                            val day = birthday.dayOfMonth
                            val month = birthday.month + 1
                            val year = birthday.year
                            var data="$day/$month/$year"
                            database.writeNewUser(name, surname,data,email)
                            Toast.makeText(applicationContext, "Send email verification", Toast.LENGTH_SHORT).show()
                            Firebase.auth.signOut()
                            startActivity(Intent(this, Login::class.java))


                        }
                        }

                    } else {
                        Toast.makeText(
                            baseContext,
                            "Signup failed" + task.exception,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

    }
    private fun addDataToDatabase() {

    }

}