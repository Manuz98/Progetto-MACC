package com.example.mobileproject.Remote

import com.example.mobileproject.Model.BookingInformation

interface IBookingInfoLoadListener {
    fun onBookingInfoLoadEmpty()

    fun onBookingInfoLoadSuccess(bookingInformation: BookingInformation)

    fun onBookingInfoLoadFailed(message: String)
}