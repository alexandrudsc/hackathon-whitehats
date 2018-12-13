package com.whitehats.bonopastore

import android.Manifest
import android.app.Service
import android.content.Intent
import android.os.IBinder

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log

import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import com.whitehats.bonopastore.main.BonoLocationListener

class BonoLocationService: Service(), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private lateinit var mLocation: Location

    private var mLocationManager: LocationManager? = null

    private var mLocationRequest: LocationRequest? = null

    private var mGoogleApiClient: GoogleApiClient? = null

    override fun onConnectionSuspended(p0: Int) {
        Log.i(TAG, "Connection Suspended")
        mGoogleApiClient?.connect()
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.errorCode)
    }

    override fun onLocationChanged(location: Location) {
        Log.e(TAG, "onLocationChanged: $location")
        this.mLocation = location
        bonoLocationListener?.locationUpdated(location)
    }

    override fun onConnected(p0: Bundle?) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        startLocationUpdates()

        var fusedLocationProviderClient:
                FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener(OnSuccessListener<Location>
            { location ->
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    // Logic to handle location object
                    mLocation = location;
                }
            })
    }

    override fun onBind(arg0: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        super.onStartCommand(intent, flags, startId)

        return Service.START_STICKY
    }

    override fun onCreate() {
        Log.d(TAG, "onCreate")
        initializeLocationManager()
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()

        if (mGoogleApiClient != null) {
            mGoogleApiClient?.connect()
        }
//        try {
//            startLocationUpdates()
//        } catch (ex: java.lang.SecurityException) {
//            Log.i(TAG, "fail to request location update, ignore", ex)
//        } catch (ex: IllegalArgumentException) {
//            Log.d(TAG, "network provider does not exist, " + ex.message)
//        }
    }

    override fun onDestroy() {
        Log.e(TAG, "onDestroy")
        super.onDestroy()
        if (mGoogleApiClient?.isConnected == true) {
            mGoogleApiClient?.disconnect()
        }
    }

    private fun initializeLocationManager() {
        Log.d(
            TAG,
            "initializeLocationManager - LOCATION_INTERVAL: $LOCATION_INTERVAL LOCATION_DISTANCE: $LOCATION_DISTANCE"
        )
        if (mLocationManager == null) {
            mLocationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
    }

    private fun startLocationUpdates() {

        // Create the location request
        mLocationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(UPDATE_INTERVAL)
            .setFastestInterval(FASTEST_INTERVAL)
        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
            mLocationRequest, this)
    }

    companion object {
        val TAG = javaClass.name
        var bonoLocationListener: BonoLocationListener? = null
        private val LOCATION_INTERVAL = 1000
        private val LOCATION_DISTANCE = 10f
        private val UPDATE_INTERVAL = (2 * 1000).toLong()  /* 10 secs */
        private val FASTEST_INTERVAL: Long = 2 * 1000 /* 2 sec */
    }
}
