package com.example.mobileproject

import android.app.AlertDialog
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
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import dmax.dialog.SpotsDialog
import java.lang.Exception
import java.text.SimpleDateFormat
import kotlinx.android.synthetic.main.nav_header.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [BookingFinal.newInstance] factory method to
 * create an instance of this fragment.
 */
class BookingFinal : Fragment() {

    private lateinit var simpleDateFormat: SimpleDateFormat
    lateinit var dialog: AlertDialog

    fun setData(view: View){
        val txt_booking_hospital_text:TextView = view.findViewById(R.id.txt_booking_hospital_text)
        val txt_booking_time_text:TextView = view.findViewById(R.id.txt_booking_time_text)
        val txt_hospital_address:TextView = view.findViewById(R.id.txt_hospital_address)
        val txt_hospital_name:TextView = view.findViewById(R.id.txt_hospital_name)
        txt_booking_hospital_text.text = Common.currentHospital.name
        txt_booking_time_text.text = StringBuilder(Common.currentTimeSlot)
            .append(" at ")
            .append(simpleDateFormat.format(Common.bookingDate.time))
        txt_hospital_name.text = Common.currentHospital.name
        txt_hospital_address.text = Common.currentHospital.address
    }

    fun confirmBooking(){
        dialog.show()
        //Process timestamp
        //we will use timestamp to filter all booking with date is greater today
        //For only display all future booking
        var startTime: String = Common.currentTimeSlot
        var convertTime: List<String> = startTime.split("-")//Split ex:9:00 - 10:00
        //Get start time: get 9:00
        var startTimeConvert: List<String> = convertTime[0].split(":")
        var startHourInt: Int = Integer.parseInt(startTimeConvert[0].trim()) //we get 9
        var startMinInt: Int = Integer.parseInt(startTimeConvert[1].trim()) //we get 00

        var bookingDateWithourHouse: Calendar = Calendar.getInstance()
        bookingDateWithourHouse.timeInMillis = Common.bookingDate.timeInMillis
        bookingDateWithourHouse.set(Calendar.HOUR_OF_DAY, startHourInt)
        bookingDateWithourHouse.set(Calendar.MINUTE, startMinInt)

        //Create timestamp object and apply to BookingInformation
        var timestamp: Timestamp = Timestamp(bookingDateWithourHouse.time)

        //create booking information
        val user = Firebase.auth.currentUser
        var bookingInformation: BookingInformation = BookingInformation()
        bookingInformation.done = false;//always false, because we will use this field to filter for display on user
        bookingInformation.timestamp = timestamp
        bookingInformation.hospitalId = Common.currentHospital.hospitalId
        bookingInformation.hospitalName = Common.currentHospital.name
        bookingInformation.hospitalAddress = Common.currentHospital.address
        bookingInformation.patientName = Common.currentUserNameSurname
        bookingInformation.patientEmail = user!!.email
        bookingInformation.time = StringBuilder(Common.currentTimeSlot)
            .append(" at ")
            .append(simpleDateFormat.format(bookingDateWithourHouse.time)).toString()
        bookingInformation.slot = Common.convertStringToTimeSlot(Common.currentTimeSlot).toLong()

        //submit to Hospital document
        var bookingdate: DocumentReference = FirebaseFirestore.getInstance()
            .collection("AllHospital")
            .document(Common.currentHospital.hospitalId.toString())
            .collection(Common.simpleDateFormat.format(Common.bookingDate.time))
            .document(Common.convertStringToTimeSlot(Common.currentTimeSlot).toString())

        //Write data
        bookingdate.set(bookingInformation).addOnSuccessListener(object: OnSuccessListener<Void>{
            override fun onSuccess(p0: Void?) {
              addToUserBooking(bookingInformation)
            }
        }).addOnFailureListener(object: OnFailureListener{
            override fun onFailure(p0: Exception) {
                Toast.makeText(context, ""+p0.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addToUserBooking(bookingInformation: BookingInformation) {
        val user = Firebase.auth.currentUser
        //First create new collection
        var userBooking: CollectionReference = FirebaseFirestore.getInstance()
            .collection("User")
            .document(user!!.email.toString())
            .collection("Booking")

        //Check if exist document in this collection
        userBooking.whereEqualTo("done",false)//if have any document with field done=false
            .get().addOnCompleteListener(object: OnCompleteListener<QuerySnapshot>{
                override fun onComplete(p0: Task<QuerySnapshot>) {
                    if(p0.result.isEmpty){
                        userBooking.document()
                            .set(bookingInformation)
                            .addOnSuccessListener(object: OnSuccessListener<Void>{
                                override fun onSuccess(p0: Void?) {
                                    if(dialog.isShowing)
                                        dialog.dismiss()
                                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                                    val recyclerViewFragment = RecyclerViewFragment()
                                    val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
                                    transaction.replace(R.id.frameLayout, HomeFragment())
                                    transaction.commit()
                                }
                            }).addOnFailureListener(object: OnFailureListener{
                                  override fun onFailure(p0: Exception) {
                                      if(dialog.isShowing)
                                          dialog.dismiss()
                                      Toast.makeText(context, p0.message, Toast.LENGTH_SHORT).show()
                                  }
                            })
                    }
                    else{
                        if(dialog.isShowing)
                            dialog.dismiss()
                        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                        val recyclerViewFragment = RecyclerViewFragment()
                        val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
                        transaction.replace(R.id.frameLayout, HomeFragment())
                        transaction.commit()
                    }
                }
            })
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
        dialog = SpotsDialog.Builder().setContext(context).setCancelable(false).build()
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