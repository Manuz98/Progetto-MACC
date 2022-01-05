package com.example.mobileproject

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileproject.Common.Common
import com.example.mobileproject.Model.SpacesItemDecoration
import com.example.mobileproject.Model.TimeSlot
import com.example.mobileproject.Remote.ITimeSlotLoadListener
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import devs.mulham.horizontalcalendar.HorizontalCalendar
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.fragment_time_slot.*
import kotlinx.android.synthetic.main.layout_time_slot.view.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 * Use the [RecyclerViewTimeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RecyclerViewTimeFragment : Fragment(), ITimeSlotLoadListener, MyTimeSlotAdapter.ClickListener{

    private lateinit var hospitalDoc: DocumentReference
    private lateinit var iTimeSlotLoadListener: ITimeSlotLoadListener
    private lateinit var dialog: AlertDialog
    //private lateinit var unbinder: Unbinder
    private lateinit var simpleDateFormat: SimpleDateFormat
    private lateinit var timeSlots: ArrayList<TimeSlot>
    private lateinit var adapter: MyTimeSlotAdapter

    fun displayTimeSlot(view: View){
        var date: Calendar = Calendar.getInstance()
        date.add(Calendar.DATE,0)
        loadAvailableTimeSlotOfHospital(view, Common.currentHospital.hospitalId.toString(),
            simpleDateFormat.format(date.time))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        iTimeSlotLoadListener = this
        simpleDateFormat = SimpleDateFormat("dd_MM_yyyy")
        dialog = SpotsDialog.Builder().setContext(context).setCancelable(false).build()
        timeSlots = ArrayList()
        arguments?.let {

        }
    }

    private fun loadAvailableTimeSlotOfHospital(view: View, hospitalId: String, bookDate: String){
        dialog.show()
        hospitalDoc = FirebaseFirestore.getInstance()
            .collection("AllHospital")
            .document(hospitalId)
        hospitalDoc.get().addOnCompleteListener(object: OnCompleteListener<DocumentSnapshot>{
           override fun onComplete(task: Task<DocumentSnapshot>){
               if(task.isSuccessful){
                   val documentSnapshot: DocumentSnapshot = task.result
                   if(documentSnapshot.exists())//if hospital available
                   {
                       //Get information of booking
                       //If not created, return empty
                       val date: CollectionReference = FirebaseFirestore.getInstance()
                           .collection("AllHospital")
                           .document(hospitalId)
                           .collection(bookDate)

                       date.get().addOnCompleteListener(object: OnCompleteListener<QuerySnapshot>{
                           override fun onComplete(task: Task<QuerySnapshot>) {
                               if(task.isSuccessful){
                                   val querySnapshot: QuerySnapshot = task.result
                                   if(querySnapshot.isEmpty)//if don't have any appointment
                                        iTimeSlotLoadListener.onTimeSlotLoadEmpty(view)
                                   else{//if have an appointment
                                        for(document: DocumentSnapshot in task.result)
                                          timeSlots.add(document.toObject(TimeSlot::class.java)!!)
                                        iTimeSlotLoadListener.onTimeSlotLoadSuccess(view,timeSlots)
                                   }
                               }
                           }
                       }).addOnFailureListener(object: OnFailureListener{
                           override fun onFailure(p0: Exception) {
                               iTimeSlotLoadListener.onTimeSlotLoadFailed(p0.message)
                           }
                       })

                       }
                   }
               }
           })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_time_slot, container, false)
        displayTimeSlot(view)
        init(view)
        return view
    }

    private fun init(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_time_slot)
        val gridLayoutManager: GridLayoutManager = GridLayoutManager(activity, 3)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.addItemDecoration(SpacesItemDecoration(8))
        val startDate: Calendar = Calendar.getInstance()
        startDate.add(Calendar.DATE,0)
        val endDate: Calendar = Calendar.getInstance()
        endDate.add(Calendar.DATE,2)
        val horizontalCalendar: HorizontalCalendar = HorizontalCalendar.Builder(view, R.id.CalendarView)
            .range(startDate,endDate)
            .datesNumberOnScreen(1)
            .mode(HorizontalCalendar.Mode.DAYS)
            .defaultSelectedDate(startDate)
            .build()

        horizontalCalendar.calendarListener = object: HorizontalCalendarListener() {
            override fun onDateSelected(date: Calendar, position: Int){
                if(Common.bookingDate.timeInMillis != date.timeInMillis){
                    Common.bookingDate = date
                   loadAvailableTimeSlotOfHospital(view, Common.currentHospital.hospitalId.toString(),
                       simpleDateFormat.format(date.time))
                }
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RecyclerViewTimeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RecyclerViewTimeFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onTimeSlotLoadSuccess(view:View, timeSlotList: ArrayList<TimeSlot>) {
        adapter = MyTimeSlotAdapter(context, timeSlotList,this)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_time_slot)
        recyclerView.adapter = adapter
        dialog.dismiss()
    }

    override fun onTimeSlotLoadFailed(message: String?) {
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
        dialog.dismiss()
    }

    override fun onTimeSlotLoadEmpty(view: View) {
        adapter = MyTimeSlotAdapter(context, this)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_time_slot)
        recyclerView.adapter = adapter
        dialog.dismiss()
    }

    override fun onItemClick(cardView: CardView) {
        Common.currentTimeSlot = cardView.txt_time_slot.text.toString()
        val bookingFinalFragment = BookingFinal()
        val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
        transaction.replace(R.id.frameLayout, bookingFinalFragment)
        transaction.commit()
    }
}