package com.whitehats.bonopastore

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.VolleyError
import com.whitehats.bonopastore.model.Friend
import com.whitehats.bonopastore.model.FriendsAdapter
import com.whitehats.bonopastore.remote.RequestResponse
import com.whitehats.bonopastore.remote.ServerConfig
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import android.content.Intent
import android.widget.EditText


class FriendsActivity : AppCompatActivity(),
  Response.Listener<JSONObject>,
  Response.ErrorListener {



    private var recyclerView: RecyclerView? = null
    private val adapter: RecyclerView.Adapter<*>? = null
    private var friendsList: MutableList<Friend>? = null
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)

        recyclerView = findViewById<RecyclerView>(R.id.recvcler_view)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerView?.adapter = adapter

        friendsList = mutableListOf<Friend>()


    }

    fun onFriendSelected(view: View) {
        var friend: Friend = friendsList!!.last()
        val resultIntent = Intent()
        resultIntent.putExtra("FRIEND_EXTRA", friend)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    fun onBtnSearchClick(view: View) {
        friendsList?.clear()
        loadUrlData()
    }

    private fun loadUrlData() {
        progressDialog = ProgressDialog(this)
        progressDialog?.setMessage("Loading...")
        progressDialog?.show()
        var txt = this.findViewById<EditText>(R.id.input_search);
        RequestResponse.sendGetRequest(this, this, this,
            ServerConfig.API_SEARCH_USERS + txt.text.toString())
    }

    override fun onResponse(response: JSONObject?) {
        progressDialog?.dismiss()
        if (response == null)
        {
            return
        }
        try {
            var array: JSONArray = response.getJSONArray("data")

            for (i in 0..(array.length() - 1))
            {
                val item = array.getJSONObject(i)
                val friend = Friend(item.getString("name"), item.getString("_id"), "")
                friendsList?.add(friend)
            }

            var adapter = FriendsAdapter(friendsList, applicationContext)
            recyclerView?.adapter = adapter

        } catch (e : JSONException) {
            e.printStackTrace()
        }
    }

    override fun onErrorResponse(error: VolleyError?) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show()
    }
}
