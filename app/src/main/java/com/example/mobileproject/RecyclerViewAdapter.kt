package com.example.mobileproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewAdapter(private val hospitalList: ArrayList<HospitalModel>, val clickListener: ClickListener): RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewAdapter.MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.nameTextView.text = hospitalList[position].name
        holder.addressTextView.text = hospitalList[position].address
        holder.itemView.setOnClickListener{
            clickListener.onItemClick(hospitalList[position])
        }
    }

    override fun getItemCount(): Int {
        return hospitalList.size
    }

    interface ClickListener{
        fun onItemClick(hospitalModel: HospitalModel)
    }

    class MyViewHolder(view: View): RecyclerView.ViewHolder(view){
        val nameTextView: TextView = view.findViewById(R.id.hospital_name)
        val addressTextView: TextView = view.findViewById(R.id.hospital_address)
    }
}