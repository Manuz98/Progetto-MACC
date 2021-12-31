package com.example.mobileproject


import android.util.Log
import com.example.mobileproject.Common.Common
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_homepage.*
import kotlinx.android.synthetic.main.fragment_profile.*
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
    fun getUserDetailProfile(fragment: ProfileFragment){
        database!!.child("users").child(uid!!).get().addOnSuccessListener {
            fragment.profilebirthday.setText(it.child("birthday").value.toString())
            fragment.profilesurname.setText(it.child("surname").value.toString())
            fragment.profilename.setText(it.child("name").value.toString())
        }
    }
    fun getUserName(activity: MainActivity){
        database!!.child("users").child(uid!!).get().addOnSuccessListener {

            activity.user_name.text=it.child("name").value.toString() +" "+ it.child("surname").value.toString()
            Common.currentUserNameSurname = activity.user_name.text.toString()
        }
    }

    fun updateUser(name: String ,  surname: String,  birthday: String?,  email: String){
        database!!.child("users").child(uid!!).child("name").setValue(name)
        database!!.child("users").child(uid!!).child("surname").setValue(surname)
        database!!.child("users").child(uid!!).child("birthday").setValue(birthday)
        database!!.child("users").child(uid!!).child("email").setValue(email)
    }

}