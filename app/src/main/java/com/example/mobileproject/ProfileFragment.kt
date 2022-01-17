package com.example.mobileproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.fragment_homepage.*
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {
    val user = Firebase.auth.currentUser



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {

        return inflater.inflate(R.layout.fragment_profile1, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val textView =profileemail
        textView.setText(user!!.email)
        val database = Database()
        database.getUserDetailProfile(this)
        profilebutton.setOnClickListener{
            var flag=true
            if(profilename.text.toString() == ""){
                profilename.error = resources.getString(R.string.name_blank)
                flag=false
            }
            if(profilesurname.text.toString() == ""){
                profilesurname.error = resources.getString(R.string.surname_blank)
                flag=false
            }
            if(profileemail.text.toString() == ""){
                profileemail.error = resources.getString(R.string.email_blank)
                flag=false

            }
            else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(profileemail.text.toString()).matches()) {
                profileemail.requestFocus()
                profileemail.error = resources.getString(R.string.email_miss_error)
                flag=false

            }
            if(profilebirthday.text.toString() == ""){
                profilebirthday.error = resources.getString(R.string.name_blank)
                flag=false

            }
            if(flag) {
                var name = profilename.text.toString().trim()
                var surname = profilesurname.text.toString().trim()
                var email = profileemail.text.toString().trim()
                var birthday = profilebirthday.text.toString().trim()
                database.updateUser(name, surname, birthday, email)
                Toast.makeText(getActivity(),"Profile updated",Toast.LENGTH_SHORT).show();

            }


        }

    }

}