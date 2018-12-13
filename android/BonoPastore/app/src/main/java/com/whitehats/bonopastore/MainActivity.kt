package com.whitehats.bonopastore


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.telephony.TelephonyManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.VolleyError
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.whitehats.bonopastore.main.MainView
import com.whitehats.bonopastore.main.Presenter
import com.whitehats.bonopastore.main.PresenterImpl
import com.whitehats.bonopastore.remote.RequestResponse
import com.whitehats.bonopastore.socketcom.Sender
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import org.json.JSONObject
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity(), OnMapReadyCallback,
    NavigationView.OnNavigationItemSelectedListener,
    Response.Listener<JSONObject>, Response.ErrorListener, MainView {

    var presenter: Presenter by Delegates.notNull()
    private var mMap: GoogleMap? = null

    private val TAG = "MainActivity"

    private var mLocationManager: LocationManager? = null

    private val DEFAULT_ZOOM: Float = 15F
    private var mLocationPermissionGranted = false

    private var user: User = User()
    private var serviceIntent : Intent? = null

    override fun showNotification(message: String) {
        val channel = NotificationChannel(CHANNEL_ID, channel_name, NotificationManager.IMPORTANCE_HIGH).apply {
            description = "Notification from firebase"
        }

        var mBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_action_background)
            .setContentTitle("Firebase message")
            .setContentText(message.toString())
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        val intent = Intent(this, MainActivity::class.java)
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.createNotificationChannel(channel)
        mNotificationManager.notify("test",0, mBuilder.build())

        var v :Vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        // Vibrate for 500 milliseconds
        v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    override fun updateLocation(location: Location) {
        var msg = "Updated Location: Latitude " + location.longitude.toString() + location.longitude

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        // The "which" argument contains the position of the selected item.

        // Add a marker for the selected place, with an info window
        // showing information about that place.
        var latLng: LatLng = LatLng(location.latitude, location.longitude)

        // Position the map's camera at the location of the marker.

        mMap?.moveCamera( CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM))
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE)
            == PackageManager.PERMISSION_GRANTED)
        {
            val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            var simNumber = tm.line1Number
            if (simNumber == "")
            {
                simNumber = tm.subscriberId
            }
            user.simNumber = simNumber
        }
        user.lastLocation = Pair(location.latitude, location.longitude)
        Sender.sendLocationMessage(user)
//        circle : Circle = map.addCircle(CircleOptions()
//        .center()
//        .radius(10000)
//        .strokeColor(Color.RED)
//        .fillColor(Color.BLUE))
    }

    companion object {
        val TAG = "MainActivity"
        val CHANNEL_ID = "0"
        val channel_name = "firebase"

        val PERMISSIONS_PHONE_NUMBER = 2
        val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 3
    }

    var token = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        presenter = PresenterImpl(this)
        BonoFirebaseMessagingService.messageListener = presenter
        BonoLocationService.bonoLocationListener = presenter


        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(MainActivity.TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                token = task.result!!.token

                // Log and toast
                //val msg = getString(R.string.msg_token_fmt, token)
                Log.d(MainActivity.TAG, token)
                Toast.makeText(this@MainActivity, token, Toast.LENGTH_SHORT).show()
            })

        mLocationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var mapFragment: MapFragment  = fragmentManager.findFragmentById(R.id.map_fragment) as MapFragment
        mapFragment.getMapAsync(this)

        serviceIntent = Intent(this, BonoLocationService::class.java)
        startService(serviceIntent)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(serviceIntent)
    }

    fun onBtnRegisterClick(btnRegister :View) {
        if (token == "") {
            return
        }

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE)
            != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale
                      (this, android.Manifest.permission.READ_PHONE_STATE))
            {
            }
            else
            {
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.READ_PHONE_STATE), PERMISSIONS_PHONE_NUMBER)
            }
        }
        else
        {
            val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            var json = JSONObject()
            var simNumber = tm.line1Number
            if (simNumber == "")
            {
                simNumber = tm.subscriberId
            }
            //json.put("name", applicationContext.getString(R.id.nav_header_title));
            json.put("name", "Alex Dascalu");
            json.put("phone", simNumber)
            json.put("email", "alex@gmail.com")
            json.put("token", token)
            user.simNumber = simNumber
            RequestResponse.sendJSONRequest(this, this, this, json)
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_PHONE_NUMBER ->
            {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    var json = JSONObject()
                    val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

                    //---get the SIM card ID---
                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE)
                      == PackageManager.PERMISSION_GRANTED)
                    {
                        var simNumber = tm.line1Number
                        if (simNumber == "")
                        {
                            simNumber = tm.subscriberId
                        }
                        json.put("name", "Alex Dascalu")
                        json.put("phone", simNumber)
                        json.put("email", "alex@gmail.com")
                        json.put("token", token)
                        user.simNumber = simNumber
                        RequestResponse.sendJSONRequest(this, this, this, json)
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION ->
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true
                }

            }
            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }


    override fun onResponse(response: JSONObject?) {

    }

    override fun onErrorResponse(error: VolleyError?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap
        updateLocationUI()
    }

    /*
    * Request location permission, so that we can get the location of the
    * device. The result of the permission request is handled by a callback,
    * onRequestPermissionsResult.
    */
    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission( this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED)
        {
            mLocationPermissionGranted = true
        }
        else
        {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    private fun updateLocationUI() {
        if (mMap == null) {
            return
        }

        try {
            if (mLocationPermissionGranted)
            {
                mMap?.isMyLocationEnabled = true
                mMap?.uiSettings?.isMyLocationButtonEnabled = true
            }
            else
            {
                mMap?.isMyLocationEnabled = false
                mMap?.uiSettings?.isMyLocationButtonEnabled = false
                getLocationPermission()
            }
        }
        catch (e: SecurityException)
        {
            Log.e("Exception: %s", e.message)
        }
    }
}