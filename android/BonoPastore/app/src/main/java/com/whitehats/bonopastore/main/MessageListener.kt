package com.whitehats.bonopastore.main

import com.google.firebase.messaging.RemoteMessage

interface MessageListener {
    fun onMessageReceived(remoteMessage: RemoteMessage?)
}