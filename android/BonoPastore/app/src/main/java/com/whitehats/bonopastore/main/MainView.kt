package com.whitehats.bonopastore.main

import android.location.Location

interface MainView {
    fun showNotification(message: String)
    fun updateLocation(location: Location)
}