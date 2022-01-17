package com.example.mobileproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileproject.Model.HospitalModel

class RecyclerViewRegionsAdapter(private val regionList: ArrayList<String>, val clickListener: ClickListener): RecyclerView.Adapter<RecyclerViewRegionsAdapter.MyViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewRegionsAdapter.MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_regions_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewRegionsAdapter.MyViewHolder, position: Int) {
        holder.nameTextView.text = regionList[position]
        holder.itemView.setOnClickListener{
            clickListener.onItemClick(regionList[position])
        }
    }

    override fun getItemCount(): Int {
        return regionList.size
    }

    interface ClickListener{
        fun onItemClick(region: String)
    }

    class MyViewHolder(view: View): RecyclerView.ViewHolder(view){
        val nameTextView: TextView = view.findViewById(R.id.region_name)
    }
}