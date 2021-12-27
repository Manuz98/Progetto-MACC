package com.example.mobileproject

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_reset_password.*

import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.nav_header.*

class ResetPassword : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
        // Initialize Firebase Auth
        auth = Firebase.auth


        Resetbutton.setOnClickListener{
            reset()
        }


    }

    private fun reset(){
        var email=Resetemail.text.toString()
        if(Resetemail.text.toString().isEmpty()){
            Resetemail.error = resources.getString(R.string.email_blank)
            Resetemail.requestFocus()
            return
        }
        Firebase.auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(applicationContext, "Send password reset email", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
