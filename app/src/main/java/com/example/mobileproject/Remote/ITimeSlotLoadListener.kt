package com.example.mobileproject.Remote

import android.view.View
import com.example.mobileproject.Model.TimeSlot

interface ITimeSlotLoadListener {

    fun onTimeSlotLoadSuccess(view: View, timeSlotList: ArrayList<TimeSlot>)

    fun onTimeSlotLoadFailed(message: String?)

    fun onTimeSlotLoadEmpty(view: View)
}