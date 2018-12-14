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
import com.android.volley.toolbox.StringRequest

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

        fun sendJSONRequest(service: Activity, listener: Response.Listener<JSONObject>, errorListener: ErrorListener,
                            json: JSONObject, url: String) {
            // Instantiate the RequestQueue.
            val queue = Volley.newRequestQueue(service)

            val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, json,
                listener, errorListener
            )

            // Add the request to the RequestQueue.
            queue.add(jsonObjectRequest)
        }

        fun sendGetRequest(service: Activity, listener: Response.Listener<JSONObject>, errorListener: ErrorListener,
                           requestStr: String)
        {
            // Instantiate the RequestQueue.
            val queue = Volley.newRequestQueue(service)
            val url = ServerConfig.hostname + ":" + ServerConfig.port + "/" + requestStr
            val request = JsonObjectRequest(Request.Method.GET, url, null,
                listener, errorListener
            )

            // Add the request to the RequestQueue.
            queue.add(request)
        }

        fun getDisasters(service: Activity, listener: Response.Listener<String>, errorListener: Response.ErrorListener)
        {
            // Instantiate the RequestQueue.
            val queue = Volley.newRequestQueue(service)
            val url = ServerConfig.hostname + ":" + ServerConfig.port + "/" + ServerConfig.API_GET_DISASTERS
            val request = StringRequest(Request.Method.GET, url,
                listener, errorListener
            )
            // Add the request to the RequestQueue.
            queue.add(request)
        }


        //fun parseResponseDisasters()

    }
}
