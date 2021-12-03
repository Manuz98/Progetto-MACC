package com.example.mobileproject

import android.Manifest
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.mobileproject.Common.Common
import com.example.mobileproject.Model.MyPlaces
import com.example.mobileproject.Remote.IGoogleAPIService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener{

    //Initialize variable
    private lateinit var map : GoogleMap
    private lateinit var lastLocation : Location
    private lateinit var fusedLocationProviderClient : FusedLocationProviderClient

    private var latitude:Double=0.toDouble()
    private var longitude:Double=0.toDouble()

    companion object{
        private const val LOCATION_REQUEST_CODE = 1
    }

    lateinit var mService:IGoogleAPIService
    internal lateinit var currentPlace:MyPlaces

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val supportMapFragment = supportFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        supportMapFragment.getMapAsync(this)

        mService = Common.googleApiService

        //Initialize fused location provider client
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true
        map.setOnMarkerClickListener(this)
        setUpMap()
    }

    private fun setUpMap() {
        //Check permission

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
            return
        }
        map.isMyLocationEnabled = true
        fusedLocationProviderClient.lastLocation.addOnSuccessListener(this){ location ->
            if(location != null){
                lastLocation = location
                val currentLatLong = LatLng(location.latitude, location.longitude)
                placeMarkerOnMap(currentLatLong)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 12f))
                nearByPlace(location.latitude, location.longitude)
            }
        }
    }

    private fun placeMarkerOnMap(currentLatLong: LatLng) {
      val markerOptions = MarkerOptions().position(currentLatLong)
      markerOptions.title("Your Position").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
      map.addMarker(markerOptions)
    }

    override fun onMarkerClick(p0: Marker) = false

    private fun nearByPlace(latitude: Double, longitude: Double) {
      //clear all marker on map
      //map.clear()
      //build URL request based on location
      val url = getUrl(latitude, longitude)

      mService.getNearbyPlaces(url).enqueue(object:Callback<MyPlaces>{
          override fun onResponse(call: Call<MyPlaces>, response: Response<MyPlaces>) {
              currentPlace = response!!.body()!!
              if(response!!.isSuccessful){
                  for(i in 0 until response!!.body()!!.results!!.size){
                      val markerOptions=MarkerOptions()
                      val googlePlace = response.body()!!.results!![i]
                      val lat = googlePlace.geometry!!.location!!.lat
                      val lng = googlePlace.geometry!!.location!!.lng
                      val placeName = googlePlace.name
                      val latLng = LatLng(lat,lng)
                      markerOptions.position(latLng)
                      markerOptions.title(placeName)
                      markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_hospital))
                      //markerOptions.snippet(i.toString())
                      //add marker to map
                      map!!.addMarker(markerOptions)
                      //move camera
                      map!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                      map!!.animateCamera(CameraUpdateFactory.zoomTo(12f))
                  }
              }
          }

          override fun onFailure(call: Call<MyPlaces>, t: Throwable) {
              Toast.makeText(baseContext, ""+t!!.message, Toast.LENGTH_SHORT).show()
          }

      })
    }

    private fun getUrl(latitude: Double, longitude: Double): String {
      val ai: ApplicationInfo = applicationContext.packageManager
            .getApplicationInfo(applicationContext.packageName, PackageManager.GET_META_DATA)
      val value = ai.metaData["com.google.android.geo.API_KEY"]
      val googlePlaceUrl = StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json")
      googlePlaceUrl.append("?location=$latitude,$longitude")
      googlePlaceUrl.append("&radius=10000") //10km
      googlePlaceUrl.append("&type=hospital")
      googlePlaceUrl.append("&key="+value.toString())

      Log.d("URL_DEBUG",googlePlaceUrl.toString())
      return googlePlaceUrl.toString()
    }

}