package com.example.mobileproject

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

class Register : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize Firebase Auth
        auth = Firebase.auth

        //Show password checkbox listener
        signuppass_check.setOnClickListener{
            showPassword()
        }
        signupbutton.setOnClickListener{
            if(checkForm()){
                signUpUser()
            }

        }


    }

    private fun checkForm(): Boolean {
        var flag = true
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


    // Show Password function, used for signup form
    private var passChecked : Boolean = true
    private fun showPassword(){

        if (passChecked){
            signuppassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            passChecked = !passChecked
            return
        }


        signuppassword.transformationMethod = PasswordTransformationMethod.getInstance()
        passChecked = !passChecked
    }

    private fun signUpUser(){
        auth.createUserWithEmailAndPassword(signupemail.text.toString(),signuppassword.text.toString())
            .addOnCompleteListener(this){ task ->
                if(task.isSuccessful){
                    //val user:FirebaseUser? = auth.currentUser
                    //user?.sendEmailVerification()
                    //?.addOnCompleteListener { task ->
                    //if (task.isSuccessful) {
                    startActivity(Intent(this, MapsActivity::class.java))
                    finish()
                    //}
                    //}

                } else {
                    Toast.makeText(baseContext, "Signup failed"+ task.exception, Toast.LENGTH_SHORT).show()
                }
            }
    }


}