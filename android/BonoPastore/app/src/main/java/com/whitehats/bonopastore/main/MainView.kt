package com.whitehats.bonopastore.main

import android.location.Location
import com.google.firebase.messaging.RemoteMessage

interface MainView {
    fun showNotification(remoteMessage: RemoteMessage?)
    fun updateLocation(location: Location)
}