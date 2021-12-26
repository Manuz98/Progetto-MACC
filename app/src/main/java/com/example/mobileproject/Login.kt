package com.example.mobileproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.widget.Toast
import com.google.android.gms.safetynet.SafetyNet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*


class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase Auth
        auth = Firebase.auth

        loginrec.setOnClickListener{
            recaptcha()
        }
        //Show password checkbox listener
        logincheck.setOnClickListener{
            showPassword()
        }
        loginbutton.setOnClickListener{
            onLogin()
        }
        swaplogsig.setOnClickListener{
            startActivity(Intent(this, Register::class.java))
        }



    }


    private var ok=false
    private fun recaptcha() {
        SafetyNet.getClient(this).verifyWithRecaptcha("6Ld3FM0dAAAAAKZGD4xrl1x7MHRZ0IJ5UzjqT46i")
            .addOnSuccessListener { recaptchaTokenResponse ->
                val captchaToken = recaptchaTokenResponse.tokenResult
                if (captchaToken != null) {
                    if (!captchaToken.isEmpty()) {
                        ok=true
                        Toast.makeText(applicationContext, "Correct captcha", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(applicationContext, "Invalid Captcha Response", Toast.LENGTH_SHORT).show()

                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(applicationContext, "Failed to Load Captcha", Toast.LENGTH_SHORT).show()
            }
    }
    // Show Password function, used for login form
    private var passChecked : Boolean = true
    private fun showPassword(){

        if (passChecked){
            loginpassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            passChecked = !passChecked
            return
        }


        loginpassword.transformationMethod = PasswordTransformationMethod.getInstance()
        passChecked = !passChecked
    }

    private fun onLogin(){
        if(!ok){
            Toast.makeText(applicationContext, "Invalid Captcha Response", Toast.LENGTH_SHORT).show()
            return
        }
        if(loginemail.text.toString().isEmpty()){
            loginemail.error = resources.getString(R.string.email_blank)
            loginemail.requestFocus()
            return
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(loginemail.text.toString()).matches()){
            loginemail.error = resources.getString(R.string.email_miss_error)
            loginemail.requestFocus()
            return
        }
        if(loginpassword.text.toString().isEmpty()){
            loginpassword.error = resources.getString(R.string.password_blank)
            loginpassword.requestFocus()
            return
        }
        auth.signInWithEmailAndPassword(loginemail.text.toString(), loginpassword.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user: FirebaseUser? = auth.currentUser
                    //updateUI(user)
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    //updateUI(null)
                }
            }
    }


    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

}