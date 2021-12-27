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


class Database {
    private val database: DatabaseReference? = Firebase.database.reference
    private val uid: String? = Firebase.auth.currentUser!!.uid
    fun writeNewUser( name: String ,  surname: String,  birthday: String?,  email: String){
        val user = User(name, surname , birthday ,email)
        if (uid != null) {
            database!!.child("users").child(uid).setValue(user)
        }

    }

}