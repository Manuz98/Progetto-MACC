package com.example.mobileproject

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.icu.number.NumberFormatter.with
import android.icu.number.NumberRangeFormatter.with
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.mobileproject.Common.Common
import com.example.mobileproject.Model.PlaceDetail
import com.example.mobileproject.Remote.IGoogleAPIService
import com.example.mobileproject.databinding.ActivityViewPlaceBinding
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ViewPlace : AppCompatActivity() {

    private lateinit var binding:ActivityViewPlaceBinding

    internal lateinit var mService:IGoogleAPIService
    var mPlace:PlaceDetail?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.title = "Nearby Hospitals"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)

        binding = ActivityViewPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Init Service
        mService= Common.googleApiService

        //Set empty for all text view
        binding.placeName.text=""
        binding.placeAddress.text=""
        binding.placeOpenHour.text=""

        binding.btnShowMap.setOnClickListener{
           //Open Map Intent to view
           val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(mPlace!!.result!!.url))
           startActivity(mapIntent)
        }

        //Load photo of place
        if(Common.currentResult!!.photos != null && Common.currentResult!!.photos!!.size > 0)
         Picasso.get()
             .load(getPhotoOfPlace(Common.currentResult!!.photos!![0].photo_reference!!,1000))
             .into(binding.photo)

        //Load rating
        if(Common.currentResult!!.rating != null)
            binding.ratingBar.rating = Common.currentResult!!.rating.toFloat()
        else
            binding.ratingBar.visibility = View.GONE

        //Load open hours
        if(Common.currentResult!!.opening_hours != null)
            binding.placeOpenHour.text="Open now : "+Common.currentResult!!.opening_hours!!.open_now
        else
            binding.placeOpenHour.visibility = View.GONE

        //Use service to fetch Address and Name
        mService.getDetailPlace(getPlaceDetailUrl(Common.currentResult!!.place_id!!))
                .enqueue(object:Callback<PlaceDetail>{
                    override fun onFailure(call: Call<PlaceDetail>, t: Throwable) {
                        Toast.makeText(baseContext, ""+t!!.message, Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(call: Call<PlaceDetail>, response: Response<PlaceDetail>){
                        mPlace = response!!.body()
                        binding.placeName.text=mPlace!!.result!!.name
                        binding.placeAddress.text=mPlace!!.result!!.formatted_address
                    }
                })

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun getPlaceDetailUrl(placeId: String): String {
        val ai: ApplicationInfo = applicationContext.packageManager
            .getApplicationInfo(applicationContext.packageName, PackageManager.GET_META_DATA)
        val value = ai.metaData["com.google.android.geo.API_KEY"]
        val url= StringBuilder("https://maps.googleapis.com/maps/api/place/details/json")
        url.append("?place_id=$placeId")
        url.append("&key="+value.toString())
        return url.toString()
    }

    private fun getPhotoOfPlace(photoReference: String, maxWidth: Int): String {
        val ai: ApplicationInfo = applicationContext.packageManager
            .getApplicationInfo(applicationContext.packageName, PackageManager.GET_META_DATA)
        val value = ai.metaData["com.google.android.geo.API_KEY"]
        val url= StringBuilder("https://maps.googleapis.com/maps/api/place/photo")
        url.append("?maxwidth=$maxWidth")
        url.append("&photoreference=$photoReference")
        url.append("&key="+value.toString())
        return url.toString()
    }
}