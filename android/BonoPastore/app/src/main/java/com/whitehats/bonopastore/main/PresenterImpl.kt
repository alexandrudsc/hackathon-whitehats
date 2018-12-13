package com.whitehats.bonopastore.main

import android.location.Location
import kotlin.properties.Delegates

import com.google.firebase.messaging.RemoteMessage

class PresenterImpl(mainView: MainView) : Presenter {
    override fun locationUpdated(location: Location) {
        view.updateLocation(location)
    }

    var view: MainView by Delegates.notNull()

    init {
        this.view = mainView
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        view.showNotification(remoteMessage.toString())
    }
}