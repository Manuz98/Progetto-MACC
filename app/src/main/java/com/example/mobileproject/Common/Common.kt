package com.example.mobileproject.Common

import com.example.mobileproject.Remote.IGoogleAPIService
import com.example.mobileproject.Remote.RetrofitClient

object Common {
    private val GOOGLE_API_URL="https://maps.googleapis.com/"
    val googleApiService:IGoogleAPIService
     get()=RetrofitClient.getClient(GOOGLE_API_URL).create(IGoogleAPIService::class.java)
}