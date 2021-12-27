package com.example.mobileproject.Common

import com.example.mobileproject.Model.HospitalModel
import com.example.mobileproject.Model.Results
import com.example.mobileproject.Remote.IGoogleAPIService
import com.example.mobileproject.Remote.RetrofitClient

object Common {
    private val GOOGLE_API_URL="https://maps.googleapis.com/"

    var currentResult:Results?=null
    lateinit var currentHospital: HospitalModel

    val googleApiService:IGoogleAPIService
     get()=RetrofitClient.getClient(GOOGLE_API_URL).create(IGoogleAPIService::class.java)

    fun convertTimeSlotToString(slot: Int):String {
        when(slot){
            0 -> return "9:00 - 9:30"
            1 -> return "9:30 - 10:00"
            2 -> return "10:00 - 10:30"
            3 -> return "10:30 - 11:00"
            4 -> return "11:00 - 11:30"
            5 -> return "11:30 - 12:00"
            6 -> return "12:00 - 12:30"
            7 -> return "12:30 - 13:00"
            8 -> return "13:00 - 13:30"
            9 -> return "13:30 - 14:00"
            10 -> return "14:00 - 14:30"
            11 -> return "14:30 - 15:00"
            12 -> return "15:00 - 15:30"
            13 -> return "15:30 - 16:00"
            14 -> return "16:00 - 16:30"
            15 -> return "16:30 - 17:00"
            16 -> return "17:00 - 17:30"
            17 -> return "17:30 - 18:00"
            18 -> return "18:00 - 18:30"
            19 -> return "18:30 - 19:00"
            else -> return "Closed"
        }
    }
}