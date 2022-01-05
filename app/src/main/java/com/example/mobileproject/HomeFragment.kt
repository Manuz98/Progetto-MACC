package com.example.mobileproject

import android.os.Bundle
import android.text.format.DateUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.mobileproject.Model.BookingInformation
import com.example.mobileproject.Remote.IBookingInfoLoadListener
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.nav_header.*
import kotlinx.android.synthetic.main.nav_header.view.*
import kotlinx.android.synthetic.main.fragment_homepage.*
import java.lang.Exception
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment(), IBookingInfoLoadListener {
    val user = Firebase.auth.currentUser
    lateinit var iBookingInfoLoadListener: IBookingInfoLoadListener


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        iBookingInfoLoadListener = this
        loadUserBooking()
        return inflater.inflate(R.layout.fragment_homepage, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textView =homeemail
        textView.text=user!!.email
        val database = Database()
        database.getUserDetailHome(this)


    }

    override fun onResume() {
        super.onResume()
        loadUserBooking()
    }

    private fun loadUserBooking() {
        var userBooking: CollectionReference = FirebaseFirestore.getInstance()
            .collection("User")
            .document(user!!.email.toString())
            .collection("Booking")

        //Get current date
        var calendar: Calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, 0)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)

        var toDayTimeStamp: Timestamp = Timestamp(calendar.time)

        //Select booking information from firebase with done=false and timestamp greater today
        userBooking.whereGreaterThanOrEqualTo("timestamp", toDayTimeStamp)
            .whereEqualTo("done", false)
            .limit(1)
            .get()
            .addOnCompleteListener(object: OnCompleteListener<QuerySnapshot>{
                override fun onComplete(p0: Task<QuerySnapshot>) {
                    if(p0.isSuccessful){
                        if(!p0.result.isEmpty){
                            for(queryDocumentSnapshot: QueryDocumentSnapshot in p0.result){
                                var bookingInformation: BookingInformation = queryDocumentSnapshot.toObject(BookingInformation::class.java)
                                iBookingInfoLoadListener.onBookingInfoLoadSuccess(bookingInformation)
                                break //Exit loop as soon as
                            }
                        }
                        else
                            iBookingInfoLoadListener.onBookingInfoLoadEmpty()
                    }
                }
            }).addOnFailureListener(object: OnFailureListener{
                override fun onFailure(p0: Exception) {
                    iBookingInfoLoadListener.onBookingInfoLoadFailed(p0.message.toString())
                }
            })
    }

    override fun onBookingInfoLoadEmpty() {
        card_booking_info.visibility = View.GONE
    }

    override fun onBookingInfoLoadSuccess(bookingInformation: BookingInformation) {
        txt_hospital.text = bookingInformation.hospitalName
        txt_hospital_address.text = bookingInformation.hospitalAddress
        var dateRemain: String = DateUtils.getRelativeTimeSpanString(bookingInformation.timestamp!!.toDate().time,
        Calendar.getInstance().timeInMillis, 0).toString()
        txt_time_remain.text = dateRemain
        card_booking_info.visibility = View.VISIBLE
    }

    override fun onBookingInfoLoadFailed(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}