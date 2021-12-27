package com.example.mobileproject.Remote

import com.example.mobileproject.Model.TimeSlot

interface ITimeSlotLoadListener {

    fun onTimeSlotLoadSuccess(timeSlotList: ArrayList<TimeSlot>)

    fun onTimeSlotLoadFailed(message: String?)

    fun onTimeSlotLoadEmpty()
}