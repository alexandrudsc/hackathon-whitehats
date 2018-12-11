package com.whitehats.bonopastore

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import android.widget.Toast
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.app.NotificationChannel
import android.app.ActivityManager
import android.content.Context

import android.view.View
import com.android.volley.Response
import com.android.volley.VolleyError

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.whitehats.bonopastore.main.MainView
import com.whitehats.bonopastore.main.Presenter
import com.whitehats.bonopastore.main.PresenterImpl
import com.whitehats.bonopastore.remote.RequestResponse
import org.json.JSONObject
import kotlin.properties.Delegates
import android.app.NotificationManager
import android.content.Intent
import android.app.PendingIntent
import android.opengl.Visibility


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    Response.Listener<JSONObject>, Response.ErrorListener, MainView{

    var presenter: Presenter by Delegates.notNull()

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
        val pi = PendingIntent.getActivity(this, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK)
        mBuilder.setContentIntent(pi)
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.createNotificationChannel(channel)
        mNotificationManager.notify("test",0, mBuilder.build())
    }

    companion object {
        val TAG = "MainActivity"
        val CHANNEL_ID = "0"
        val channel_name = "firebase"
    }

    var token = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        presenter = PresenterImpl(this)
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        BonoFirebaseMessagingService.messageListener = presenter



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

    fun onBtnRegisterClick(btnRegister :View) {
        if (token == "") {
            return
        }

        var json = JSONObject()
        json.put("name", "Alex Dascalu")
        json.put("phone", "0123456789")
        json.put("email", "alex@gmail.com")
        json.put("token", token)
        RequestResponse.sendJSONRequest(this, this, this, json)


    }

    override fun onResponse(response: JSONObject?) {

    }

    override fun onErrorResponse(error: VolleyError?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
