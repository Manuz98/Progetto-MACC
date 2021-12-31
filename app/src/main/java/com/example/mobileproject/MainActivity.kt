package com.example.mobileproject

import android.Manifest
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.mobileproject.Common.Common
import com.example.mobileproject.Model.MyPlaces
import com.example.mobileproject.Remote.IGoogleAPIService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_homepage.*
import kotlinx.android.synthetic.main.nav_header.*
import kotlinx.android.synthetic.main.nav_header.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout

    //Maps
    companion object {
        private const val LOCATION_REQUEST_CODE = 1
    }
    private lateinit var fusedLocationProviderClient : FusedLocationProviderClient
    lateinit var mService: IGoogleAPIService
    lateinit var map : GoogleMap
    private lateinit var lastLocation : Location
    internal lateinit var currentPlace: MyPlaces
    private lateinit var auth: FirebaseAuth




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Initialize fused location provider client
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        mService = Common.googleApiService

        drawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        toggle.drawerArrowDrawable.color = resources.getColor(R.color.mainText)
        replaceFragment(HomeFragment(), "Home")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navView.setNavigationItemSelectedListener {

            it.isChecked = true

            when(it.itemId){

                R.id.nav_home -> {
                    replaceFragment(HomeFragment(), it.title.toString())
                }
                R.id.nav_maps -> {
                    replaceFragment(MapsFragment(), it.title.toString())
                }
                R.id.nav_book -> {
                    replaceFragment(RecyclerViewFragment(), it.title.toString())
                }
                R.id.nav_profile -> {
                    replaceFragment(ProfileFragment(), it.title.toString())
                }

                R.id.nav_logout -> {
                    Toast.makeText(applicationContext, "Logout", Toast.LENGTH_SHORT).show()
                    Firebase.auth.signOut()
                    startActivity(Intent(this, Login::class.java))
                }
            }

            true

        }


    }

    private fun replaceFragment(fragment: Fragment, title: String){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()

        drawerLayout.closeDrawers()
        setTitle(title)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val user = Firebase.auth.currentUser
        if(toggle.onOptionsItemSelected(item)){
            val email=user_email
            email.setText(user!!.email)
            val database = Database()
            database.getUserName(this)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    //everything below is about Maps

    private fun placeMarkerOnMap(currentLatLong: LatLng) {
        val markerOptions = MarkerOptions().position(currentLatLong)
        markerOptions.title("Your Position").icon(
            BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_AZURE))
        map.addMarker(markerOptions)
    }


    fun setUpMap() {
        //Check permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED){
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
        map!!.setOnMarkerClickListener { marker ->
            //When user select marker, just get result of place assign to static variable
            Common.currentResult = currentPlace!!.results!![Integer.parseInt(marker.snippet)]
            //Start new activity
            startActivity(Intent(this@MainActivity, ViewPlace::class.java))
            true
        }
    }

    private fun nearByPlace(latitude: Double, longitude: Double) {
        //clear all marker on map
        map.clear()
        //build URL request based on location
        val url = getUrl(latitude, longitude)

        mService.getNearbyPlaces(url).enqueue(object: Callback<MyPlaces> {
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
                        markerOptions.snippet(i.toString())
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