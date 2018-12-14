package com.whitehats.bonopastore

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
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
import com.android.volley.Response
import com.android.volley.VolleyError
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.RemoteMessage
import com.whitehats.bonopastore.main.MainView
import com.whitehats.bonopastore.main.Presenter
import com.whitehats.bonopastore.main.PresenterImpl
import com.whitehats.bonopastore.model.Disaster
import com.whitehats.bonopastore.model.Friend
import com.whitehats.bonopastore.remote.RequestResponse
import com.whitehats.bonopastore.remote.ServerConfig
import com.whitehats.bonopastore.socketcom.Sender
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import org.json.JSONObject
import java.lang.Exception
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity(), OnMapReadyCallback,
    NavigationView.OnNavigationItemSelectedListener,
    Response.Listener<JSONObject>, Response.ErrorListener, MainView {


    var presenter: Presenter by Delegates.notNull()
    private var mMap: GoogleMap? = null

    private val DEFAULT_ZOOM: Float = 15F
    private var mLocationPermissionGranted = false

    private var user: User = User()
    private var serviceIntent : Intent? = null

    private var meCircle: Circle? = null
    private var circlesInitialDisasters: MutableMap<String, Circle?> = mutableMapOf<String, Circle?>()
    private var receivedDisasters: MutableMap<String, Circle?> = mutableMapOf<String, Circle?>()

    override fun showNotification(message: RemoteMessage?) {
        val channel =
            NotificationChannel(CHANNEL_ID, channel_name, NotificationManager.IMPORTANCE_HIGH).apply {
            description = "Notification from firebase"
        }

        try
        {
            var jsonStr = JSONObject(message?.data)
            var json = JSONObject(jsonStr.get("msg").toString())
            var message = json.get("message")
            var disaster: Disaster = Disaster.createDisaster(
                json.getJSONObject("metainfo"))
            runOnUiThread( object :Runnable{
                override fun run() {
                    Log.i(TAG,"runOnUiThread")
                    drawDisaster(disaster)
                }
            })

            var mBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notify_panel_notification_icon_bg)
                .setContentTitle(disaster.title)
                .setContentText(message?.toString())
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setColor(disaster.getColor())
                .setAutoCancel(true)

            val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            mNotificationManager.createNotificationChannel(channel)
            val notification = mBuilder.build()
            mNotificationManager.notify("test",10, notification)

            var v :Vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            // Vibrate for 500 milliseconds
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }

    }

    override fun updateLocation(location: Location) {
        // Add a marker for the selected place, with an info window
        // showing information about that place.
        var latLng: LatLng = LatLng(location.latitude, location.longitude)

        // Position the map's camera at the location of the marker.
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
        if (meCircle == null)
        {
            mMap?.moveCamera( CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM))
            meCircle =  mMap?.addCircle(
                CircleOptions()
                    .center(latLng)
                    .radius(100.0)
                    .zIndex(1F)
                    .fillColor(0x220000FF))
        }

    }

    companion object {
        const val TAG = "MainActivity"
        const val CHANNEL_ID = "10"
        const val channel_name = "firebase"
        const val PERMISSIONS_PHONE_NUMBER = 2
        const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 3
        const val REQUEST_CODE_SELECT_FRIEND = 11
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
                //Toast.makeText(this@MainActivity, token, Toast.LENGTH_SHORT).show()
            })

        var mapFragment: MapFragment  = fragmentManager.findFragmentById(R.id.map_fragment) as MapFragment
        mapFragment.getMapAsync(this)

        serviceIntent = Intent(this, BonoLocationService::class.java)
        startService(serviceIntent)
    }

    override fun onResume() {
        super.onResume()
        user.name = get_username()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_SELECT_FRIEND -> {
                if (resultCode == Activity.RESULT_OK)
                {
                    var friend: Friend = data?.extras!!["FRIEND_EXTRA"] as Friend
                    addFriend(friend)
                }
            }
        }
    }

    private fun addFriend(friend: Friend) {

        Log.d(TAG, friend.username + " " + friend.user_id)
        var json = JSONObject()
        var jsonFriend = JSONObject()

        jsonFriend.put("name", friend.username)
        jsonFriend.put("phone", friend.user_id)
        json.put("friend", jsonFriend)
        val url = ServerConfig.hostname + ":" + ServerConfig.port + "/" + ServerConfig.API_ADD_FRIEND + "/" +
                user.simNumber + "/friends"
        RequestResponse.sendJSONRequest(this, this, this, json, url);
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(serviceIntent)
    }

    fun onBtnAddFriendsClick(btnAddFriends :MenuItem) {
        var intent = Intent(this, FriendsActivity::class.java)
        startActivityForResult(intent, REQUEST_CODE_SELECT_FRIEND)
    }

    fun onBtnAddObjectsClick(btnAddFriends :MenuItem) {
    }

    fun onBtnRegisterClick(btnRegister :MenuItem) {
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
            json.put("name", user.name)
            json.put("phone", simNumber)
            json.put("email", "alex@gmail.com")
            json.put("token", token)
            user.simNumber = simNumber
            RequestResponse.sendJSONRequest(this, this, this, json)
        }

    }

    private fun get_username(): String {
        var name : String = ""
        if (Build.MODEL == "SM-G950U1") {
            name = "Alexandru Duduman"
        } else if (Build.MODEL == "Nexus 5X") {
            name = "Alexandru Dascalu"
        } else {
            name = "Sebastian Pamparau"
        }
        return name
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
                        json.put("name", user.name)
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
        Log.d(TAG, response.toString())
        if (response?.get("id") == "0") {
            val disasters = Disaster.parseDisasters(response.getJSONArray("data"))
            for(c: MutableMap.MutableEntry<String,Circle?> in circlesInitialDisasters)
            {
                if (c.value != null)
                    c.value?.remove()
            }
            for (d: Disaster in disasters!!) {
                var c: Circle? = drawDisaster(d)
                circlesInitialDisasters.put(d.id, c)
            }
        }
    }

    private fun drawDisaster(d: Disaster): Circle? {
        var c: Circle? = mMap?.addCircle(
            CircleOptions()
                .center(d.latLng)
                .radius(d.radius)
                .zIndex(3F)
                .fillColor(d.getColor()))
        receivedDisasters.put(d.id, c)
        return c;
    }

    private fun removeDisaster(d: Disaster) {
        for(c: MutableMap.MutableEntry<String,Circle?> in circlesInitialDisasters)
        {
            if (c.value != null && c.key == d.id)
            {
                c.value?.remove()
                return
            }

        }
    }

    override fun onErrorResponse(error: VolleyError?) {
        Log.e(TAG, error.toString())
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap
        RequestResponse.sendGetRequest(this, this, this,
            ServerConfig.API_GET_DISASTERS)
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