package com.example.mobileproject

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileproject.Common.Common
import com.example.mobileproject.Model.TimeSlot
import kotlinx.android.synthetic.main.layout_time_slot.view.*

class MyTimeSlotAdapter(): RecyclerView.Adapter<MyTimeSlotAdapter.MyViewHolder>(){

    private lateinit var timeSlotList: ArrayList<TimeSlot>
    private lateinit var context: Context
    lateinit var clickListener: ClickListener
    private lateinit var cardViewList: ArrayList<CardView>

    constructor(contex: Context?, timeSlotLis: ArrayList<TimeSlot>, clickListene: ClickListener) : this() {
        timeSlotList = timeSlotLis
        clickListener = clickListene
        cardViewList = ArrayList()
        if (contex != null) {
            context = contex
        }
    }

    constructor(contex: Context?, clickListene: ClickListener) : this() {
        timeSlotList = ArrayList()
        clickListener = clickListene
        cardViewList = ArrayList()
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
                    //We will set tag for all time slot is full
                    //So base on tag, we can set all remain card background without change full time slot
                    holder.cardTimeSlot.tag = "DISABLE"
                    holder.descriptionTextView.text = "Full"
                    holder.cardTimeSlot.setCardBackgroundColor(ContextCompat.getColor(context, R.color.textAlt))
                }
            }
        }

        if(!cardViewList.contains(holder.cardTimeSlot)){
            cardViewList.add(holder.cardTimeSlot)
        }

        holder.itemView.setOnClickListener{
            if(holder.cardTimeSlot.tag == null) {
                clickListener.onItemClick(cardViewList[position])
            }
            else
              Toast.makeText(context, "Not possible", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return 20
    }

    interface ClickListener {
        fun onItemClick(cardView: CardView)
    }

    class MyViewHolder(view: View): RecyclerView.ViewHolder(view){
        val timeTextView: TextView = view.findViewById(R.id.txt_time_slot)
        val descriptionTextView: TextView = view.findViewById(R.id.txt_time_slot_description)
        val cardTimeSlot: CardView = view.findViewById(R.id.card_time_slot)
    }


}