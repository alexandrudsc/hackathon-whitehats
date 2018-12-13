package com.whitehats.bonopastore.main

import android.location.Location
import com.google.firebase.messaging.RemoteMessage

interface MessageListener {
    fun onMessageReceived(remoteMessage: RemoteMessage?)
    fun locationUpdated(location: Location)
}