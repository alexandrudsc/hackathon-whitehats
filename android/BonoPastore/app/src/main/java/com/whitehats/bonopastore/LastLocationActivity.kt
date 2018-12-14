package com.whitehats.bonopastore

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.gms.maps.*

import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import java.io.Serializable


class LastLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    var lats: ArrayList<Double> = arrayListOf<Double>()
    var longs: ArrayList<Double> = arrayListOf<Double>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_last_location)
        var mapFragment: MapFragment  = fragmentManager.findFragmentById(R.id.map_last_locations) as MapFragment
        mapFragment.getMapAsync(this)

        lats.addAll(intent.getSerializableExtra("lats") as ArrayList<Double>)
        longs.addAll(intent.getSerializableExtra("longs") as ArrayList<Double>)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        var options = PolylineOptions()
        options.color(Color.RED)
        options.width(5f)

        var latLng = LatLng (0.0,0.0)
        for (i in lats.indices) {
            latLng = LatLng (lats[i], longs[i])
            options.add(latLng)
            mMap.addMarker(MarkerOptions().position(latLng))
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
    }
}
