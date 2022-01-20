package com.example.mobileproject

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.text.format.DateUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.example.mobileproject.Common.Common
import com.example.mobileproject.Model.BookingInformation
import com.example.mobileproject.Remote.IBookingInfoLoadListener
import com.example.mobileproject.Remote.IBookingInformationChangeListener
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.nav_header.*
import kotlinx.android.synthetic.main.nav_header.view.*
import kotlinx.android.synthetic.main.fragment_homepage.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_homepage.btn_change_booking
import kotlinx.android.synthetic.main.fragment_homepage.btn_delete_booking
import kotlinx.android.synthetic.main.fragment_homepage.card_booking_info
import kotlinx.android.synthetic.main.fragment_homepage.homeemail
import kotlinx.android.synthetic.main.fragment_homepage.switch_notification
import kotlinx.android.synthetic.main.fragment_homepage.txt_hospital
import kotlinx.android.synthetic.main.fragment_homepage.txt_hospital_address
import kotlinx.android.synthetic.main.fragment_homepage.txt_time_remain
import kotlinx.android.synthetic.main.fragment_homepage1.*
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
class HomeFragment : Fragment(), IBookingInfoLoadListener, IBookingInformationChangeListener {
    val user = Firebase.auth.currentUser
    lateinit var iBookingInfoLoadListener: IBookingInfoLoadListener
    lateinit var iBookingInformationChangeListener: IBookingInformationChangeListener
    lateinit var dialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        iBookingInfoLoadListener = this
        iBookingInformationChangeListener = this
        loadUserBooking()
        dialog = SpotsDialog.Builder().setContext(context).setCancelable(false).build()
        return inflater.inflate(R.layout.fragment_homepage1, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).nav_view.setCheckedItem(R.id.nav_home)
        val textView =homeemail
        textView.text=user!!.email
        val database = Database()
        database.getUserDetailHome(this)

        Log.d("SWITCH status", (activity as MainActivity).myPreferences.getBoolean((activity as MainActivity).SWITCH_STATUS, false).toString())
        switch_notification.isChecked = (activity as MainActivity).myPreferences.getBoolean((activity as MainActivity).SWITCH_STATUS, false)

        if(Common.currentTimeBooked > 0){
            (activity as MainActivity).myEditor.putLong((activity as MainActivity).TIME_BOOKED_STATUS, Common.currentTimeBooked)
            (activity as MainActivity).myEditor.apply()
        }
        var time: Long =  (activity as MainActivity).myPreferences.getLong((activity as MainActivity).TIME_BOOKED_STATUS, 0)
        Log.d("TIME inside home fragment", time.toString())

        switch_notification.setOnClickListener {
            if(switch_notification.isChecked) {
                if(time > 0) {
                    (activity as MainActivity).createAlarm()
                    (activity as MainActivity).myEditor.putBoolean((activity as MainActivity).SWITCH_STATUS, true)
                    (activity as MainActivity).myEditor.apply()
                }
            }
            else {
                (activity as MainActivity).cancelAlarm()
                (activity as MainActivity).myEditor.putBoolean((activity as MainActivity).SWITCH_STATUS, false)
                (activity as MainActivity).myEditor.apply()
            }
        }

        btn_delete_booking.setOnClickListener {
            //deleteBookingFromHospital(false)
            showConfirmDialogBeforeDelete()
        }
        btn_change_booking.setOnClickListener {
            changeBookingFromUser()
        }

        val rotate: Animation = AnimationUtils.loadAnimation(context,R.anim.rotate)
        image_home.startAnimation(rotate)
    }

    private fun showConfirmDialogBeforeDelete(){
        var confirmDialog: AlertDialog.Builder = AlertDialog.Builder(activity)
            .setTitle("Hey!")
            .setMessage("Do you really want to delete your booking?")
            .setNegativeButton("NO", object: DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dialog!!.dismiss()

                }
            }).setPositiveButton("YES", object: DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    deleteBookingFromHospital(false) //True because we call from button change
                }
            })
        confirmDialog.show()
    }

    private fun changeBookingFromUser() {
        //Show dialog confirm
        var confirmDialog: AlertDialog.Builder = AlertDialog.Builder(activity)
            .setTitle("Hey!")
            .setMessage("Do you really want to change your booking?\nYou will delete your old booking.\n\nConfirm?")
            .setNegativeButton("NO", object: DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dialog!!.dismiss()

                }
            }).setPositiveButton("YES", object: DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    deleteBookingFromHospital(true) //True because we call from button change
                }
            })
        confirmDialog.show()
    }

    private fun deleteBookingFromHospital(isChange: Boolean) {
        /*To delete booking, first we need delete from Hospital collection
          After that, we will delete from User booking collection
          And final, delete event*/

        //We need load Common.currentBooking because we need some data from BookingInformation
        if(Common.currentBooking!=null){
           dialog.show()
           //Get booking information in hospital object
           var hospitalBookingInfo: DocumentReference = FirebaseFirestore.getInstance()
               .collection("AllHospital")
               .document(Common.currentBooking.hospitalId.toString())
               .collection(Common.convertTimeStampToStringKey(Common.currentBooking.timestamp))
               .document(Common.currentBooking.slot.toString())

            //When we have document just delete it
            hospitalBookingInfo.delete().addOnFailureListener(object: OnFailureListener{
                override fun onFailure(p0: Exception) {
                    Toast.makeText(context, p0.message, Toast.LENGTH_SHORT).show()
                }

            }).addOnSuccessListener(object: OnSuccessListener<Void>{
                override fun onSuccess(p0: Void?) {
                    //After delete on Hospital
                    //We will start delete from User
                    deleteBookingFromUser(isChange)
                }

            })
        }
        else{
            Toast.makeText(context, "Current booking must not be null", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteBookingFromUser(isChange: Boolean) {
        //First, we need get information from user object
        if(!TextUtils.isEmpty(Common.currentBookingId)){
            var userBookingInfo: DocumentReference = FirebaseFirestore.getInstance()
                .collection("User")
                .document(user!!.email.toString())
                .collection("Booking")
                .document(Common.currentBookingId)

            //Delete
            userBookingInfo.delete().addOnFailureListener(object: OnFailureListener {
                override fun onFailure(p0: Exception) {
                    Toast.makeText(context, p0.message, Toast.LENGTH_SHORT).show()
                }
            }).addOnSuccessListener(object: OnSuccessListener<Void>{
                override fun onSuccess(p0: Void?) {
                    //After delete on User
                    Toast.makeText(context, "Success delete booking !", Toast.LENGTH_SHORT).show()

                    Common.currentTimeBooked = 0

                    (activity as MainActivity).cancelAlarm() //Delete the notification
                    (activity as MainActivity).myEditor.putBoolean((activity as MainActivity).SWITCH_STATUS, false)
                    (activity as MainActivity).myEditor.putLong((activity as MainActivity).TIME_BOOKED_STATUS, 0)
                    (activity as MainActivity).myEditor.apply()

                    //Once the reservation is deleted, I put the variable to true
                    Common.bookable = true

                    //Refresh
                    loadUserBooking()

                    //Check if isChange -> Call from change button we will fired interface
                    if(isChange)
                        iBookingInformationChangeListener.onBookingInformationChange()

                    dialog.dismiss()
                }
            })
        }
        else{
            dialog.dismiss()
            Toast.makeText(context, "Booking information ID must not be empty", Toast.LENGTH_SHORT).show()
        }
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
                                iBookingInfoLoadListener.onBookingInfoLoadSuccess(bookingInformation, queryDocumentSnapshot.id)
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
        if(card_booking_info != null)
           card_booking_info.visibility = View.GONE
    }

    override fun onBookingInfoLoadSuccess(bookingInformation: BookingInformation, bookingId: String) {
        Common.currentBooking = bookingInformation
        Common.currentBookingId = bookingId
        txt_hospital.text = bookingInformation.hospitalName
        txt_hospital_address.text = bookingInformation.hospitalAddress
        var dateRemain: String = DateUtils.getRelativeTimeSpanString(bookingInformation.timestamp!!.toDate().time,
        Calendar.getInstance().timeInMillis, 0).toString()
        txt_time_remain.text = bookingInformation.time
        card_booking_info.visibility = View.VISIBLE

        dialog.dismiss()
    }

    override fun onBookingInfoLoadFailed(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onBookingInformationChange() {
        //Here we will just replace HomeFragment with RecyclerViewFragment(hospital list)
        val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
        transaction.replace(R.id.frameLayout, RecyclerViewFragment())
        transaction.commit()
    }
}