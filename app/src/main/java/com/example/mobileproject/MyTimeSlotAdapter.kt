package com.example.mobileproject

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileproject.Common.Common
import com.example.mobileproject.Model.TimeSlot

class MyTimeSlotAdapter(): RecyclerView.Adapter<MyTimeSlotAdapter.MyViewHolder>()  {

    private lateinit var timeSlotList: ArrayList<TimeSlot>
    private lateinit var context: Context

    constructor(contex: Context?, timeSlotLis: ArrayList<TimeSlot>) : this() {
      timeSlotList = timeSlotLis
        if (contex != null) {
            context = contex
        }
    }

    constructor(contex: Context?) : this() {
        timeSlotList = ArrayList()
        if (contex != null) {
            context = contex
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyTimeSlotAdapter.MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_time_slot, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.timeTextView.text = StringBuilder(Common.convertTimeSlotToString(position)).toString()
        if(timeSlotList.size == 0)//If all position is available, just show list
        {
            holder.descriptionTextView.text = "Available"
            //holder.cardTimeSlot.setCardBackgroundColor(ContextCompat.getColor(context, R.color.mainBackground))
        }
        else{//if position is full(booked)
            for(slotValue: TimeSlot in timeSlotList){
                //Loop all time slot from server and set different color
                var slot = Integer.parseInt(slotValue.slot.toString())
                if(slot == position){
                    holder.descriptionTextView.text = "Full"
                    holder.cardTimeSlot.setCardBackgroundColor(ContextCompat.getColor(context, R.color.textAlt))
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return 20
    }

    class MyViewHolder(view: View): RecyclerView.ViewHolder(view){
        val timeTextView: TextView = view.findViewById(R.id.txt_time_slot)
        val descriptionTextView: TextView = view.findViewById(R.id.txt_time_slot_description)
        val cardTimeSlot: CardView = view.findViewById(R.id.card_time_slot)
    }
}