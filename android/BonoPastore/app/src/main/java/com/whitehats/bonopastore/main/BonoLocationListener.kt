package com.whitehats.bonopastore.main

import android.location.Location

interface BonoLocationListener {
    fun locationUpdated(location: Location)
}