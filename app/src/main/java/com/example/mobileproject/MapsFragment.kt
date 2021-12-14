package com.example.mobileproject


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker


/**
 * A simple [Fragment] subclass.
 * Use the [MapsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MapsFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_maps, container, false)

        val supportMapFragment = childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        supportMapFragment.getMapAsync(this)

        return view
    }

    override fun onMarkerClick(p0: Marker) = false

    override fun onMapReady(googleMap: GoogleMap) {
        (activity as MainActivity).map = googleMap
        (activity as MainActivity).map.uiSettings.isZoomControlsEnabled = true
        (activity as MainActivity).map.setOnMarkerClickListener(this)
        (activity as MainActivity).setUpMap()
    }

}