package com.whitehats.bonopastore.main

import kotlin.properties.Delegates

import com.google.firebase.messaging.RemoteMessage

class PresenterImpl(mainView: MainView) : Presenter {

    var view: MainView by Delegates.notNull()

    init {
        this.view = mainView
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        view.showNotification(remoteMessage.toString())
    }
}