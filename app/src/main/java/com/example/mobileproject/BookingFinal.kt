package com.example.mobileproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import com.example.mobileproject.Common.Common
import com.example.mobileproject.Model.BookingInformation
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.lang.Exception
import java.text.SimpleDateFormat
import kotlinx.android.synthetic.main.nav_header.*

/**
 * A simple [Fragment] subclass.
 * Use the [BookingFinal.newInstance] factory method to
 * create an instance of this fragment.
 */
class BookingFinal : Fragment() {

    private lateinit var simpleDateFormat: SimpleDateFormat

    fun setData(view: View){
        val txt_booking_hospital_text:TextView = view.findViewById(R.id.txt_booking_hospital_text)
        val txt_booking_time_text:TextView = view.findViewById(R.id.txt_booking_time_text)
        val txt_hospital_address:TextView = view.findViewById(R.id.txt_hospital_address)
        val txt_hospital_name:TextView = view.findViewById(R.id.txt_hospital_name)
        txt_booking_hospital_text.text = Common.currentHospital.name
        txt_booking_time_text.text = StringBuilder(Common.currentTimeSlot)
            .append(" at ")
            .append(simpleDateFormat.format(Common.currentDate.time))
        txt_hospital_name.text = Common.currentHospital.name
        txt_hospital_address.text = Common.currentHospital.address
    }

    fun confirmBooking(){
        //create booking information
        val user = Firebase.auth.currentUser
        var bookingInformation: BookingInformation = BookingInformation()
        bookingInformation.hospitalId = Common.currentHospital.hospitalId
        bookingInformation.hospitalName = Common.currentHospital.name
        bookingInformation.hospitalAddress = Common.currentHospital.address
        bookingInformation.patientName = Common.currentUserNameSurname
        bookingInformation.patientEmail = user!!.email
        bookingInformation.time = StringBuilder(Common.currentTimeSlot)
            .append(" at ")
            .append(simpleDateFormat.format(Common.currentDate.time)).toString()
        bookingInformation.slot = Common.convertStringToTimeSlot(Common.currentTimeSlot).toLong()

        //submit to Hospital document
        var bookingdate: DocumentReference = FirebaseFirestore.getInstance()
            .collection("AllHospital")
            .document(Common.currentHospital.hospitalId.toString())
            .collection(Common.simpleDateFormat.format(Common.currentDate.time))
            .document(Common.convertStringToTimeSlot(Common.currentTimeSlot).toString())

        //Write data
        bookingdate.set(bookingInformation).addOnSuccessListener(object: OnSuccessListener<Void>{
            override fun onSuccess(p0: Void?) {
              Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
              val recyclerViewFragment = RecyclerViewFragment()
              val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
              transaction.replace(R.id.frameLayout, recyclerViewFragment)
              transaction.commit()
            }
        }).addOnFailureListener(object: OnFailureListener{
            override fun onFailure(p0: Exception) {
                Toast.makeText(context, ""+p0.message, Toast.LENGTH_SHORT).show()
            }
        })
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_booking_final, container, false)
        val btn_confirm = view.findViewById<Button>(R.id.btn_confirm)
        btn_confirm.setOnClickListener {
            confirmBooking()
        }
        setData(view)
        return view
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BookingFinal.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BookingFinal().apply {
                arguments = Bundle().apply {

                }
            }
    }
}