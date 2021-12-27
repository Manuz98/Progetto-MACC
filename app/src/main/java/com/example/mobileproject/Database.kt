package com.example.mobileproject


import android.app.Activity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_register.view.*
import kotlinx.android.synthetic.main.fragment_homepage.*
import kotlinx.android.synthetic.main.nav_header.*


class Database {
    private val database: DatabaseReference? = Firebase.database.reference
    private val uid: String? = Firebase.auth.currentUser!!.uid
    fun writeNewUser( name: String ,  surname: String,  birthday: String?,  email: String){
        val user = User(name, surname , birthday ,email)
        if (uid != null) {
            database!!.child("users").child(uid).setValue(user)
        }

    }


    fun getUserDetailHome(fragment: HomeFragment){
        database!!.child("users").child(uid!!).get().addOnSuccessListener {
            fragment.homebirthday.text=it.child("birthday").value.toString()
            fragment.homesurname.text=it.child("surname").value.toString()
            fragment.homename.text=it.child("name").value.toString()
        }
    }
    fun getUserName(activity: MainActivity){
        database!!.child("users").child(uid!!).get().addOnSuccessListener {

            activity.user_name.text=it.child("name").value.toString() +" "+ it.child("surname").value.toString()
        }
    }

}