package com.whitehats.bonopastore

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.android.volley.Response
import com.android.volley.VolleyError
import com.whitehats.bonopastore.remote.RequestResponse
import com.whitehats.bonopastore.remote.ServerConfig
import org.json.JSONObject

class ChangeNotifyLevelActivity : AppCompatActivity(),

    Response.Listener<JSONObject>, Response.ErrorListener, View.OnClickListener {

    var user_id: String = ""

    override fun onErrorResponse(error: VolleyError?) {

    }

    override fun onResponse(response: JSONObject?) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_notify_level)
        user_id = intent.getStringExtra("user_id");
        var btn = findViewById<Button>(R.id.btnChangeNotifyLevel)
        btn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        var json: JSONObject = JSONObject()
        var editText: EditText = findViewById<EditText>(R.id.editTextDialogUserInput)
        var url: String = ServerConfig.hostname + ":" + ServerConfig.port + "/" +
                ServerConfig.API_USER + "/" + user_id + "/notify_level"
        json.put("notify_level", editText.text.toString())
        RequestResponse.sendJSONRequest(this, this, this, json, url)
        //setResult(editText.text.toString() as Int)
        finish()
    }
}
