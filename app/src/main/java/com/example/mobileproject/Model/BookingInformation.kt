package com.example.mobileproject.Model

import com.google.firebase.Timestamp

data class BookingInformation(var patientName: String?=null,var patientEmail: String?=null,
                              var time: String?=null, var hospitalId: String?=null,
                              var hospitalName: String?=null, var hospitalAddress: String?=null,
                              var slot: Long?=null, var timestamp: Timestamp?=null, var done: Boolean?=null)