package com.example.mobileproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewAdapter(val listData: List<DataModel>, val clickListener: ClickListener): RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewAdapter.MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_row, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    class MyViewHolder(view: View): RecyclerView.ViewHolder(view){
      var titleTextView: TextView
      init {
          titleTextView = view.findViewById(R.id.titleTextView)
      }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.titleTextView.text = listData.get(position).title
        holder.itemView.setOnClickListener{
            clickListener.onItemClick(listData.get(position))
        }
    }

    interface ClickListener{
        fun onItemClick(dataModel: DataModel)
    }
}