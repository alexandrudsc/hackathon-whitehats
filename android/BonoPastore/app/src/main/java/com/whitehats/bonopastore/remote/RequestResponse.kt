package com.whitehats.bonopastore.remote

import android.app.Activity
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService

import com.android.volley.toolbox.Volley
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.Response.ErrorListener
import com.android.volley.Response.Listener

import org.json.JSONObject


class RequestResponse {

    companion object {
        val TAG = "RequestResponse"

        fun sendJSONRequest(service: Activity, listener: Response.Listener<JSONObject>, errorListener: ErrorListener,
                            json: JSONObject) {
            // Instantiate the RequestQueue.
            val queue = Volley.newRequestQueue(service)
            val url = ServerConfig.hostname + ":" + ServerConfig.port + "/" + ServerConfig.API_ADD_USER

            val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, json,
                listener, errorListener
            )

            // Add the request to the RequestQueue.
            queue.add(jsonObjectRequest)
        }
    }
}
