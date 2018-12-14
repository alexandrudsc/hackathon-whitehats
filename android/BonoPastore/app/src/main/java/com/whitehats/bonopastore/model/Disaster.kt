package com.whitehats.bonopastore.model

import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray
import org.json.JSONObject

class Disaster {

    companion object {
        const val MEDIUM = 3
        const val DANGER = 6

        fun parseDisasters(array: JSONArray?) : MutableList<Disaster>? {
            var disasters : MutableList<Disaster> = mutableListOf<Disaster>()
            for (i in 0..(array!!.length() - 1)) {
                disasters.add(createDisaster(array.getJSONObject(i)))
            }
            return disasters
        }

        fun createDisaster(json: JSONObject):  Disaster {
            var d : Disaster = Disaster()
            try {
                d.level = json.getInt("level")

                d.notifier = json.getString("notifier")
                d.title = json.getString("title")
                d.radius = json.getDouble("radius")
                d.id = json.getString("_id")

                var location: JSONObject = json.getJSONObject("location")
                var coords: JSONArray = location.getJSONArray("coordinates")
                val longitude: Double = coords[0] as Double
                val latitude: Double = coords[1] as Double
                var latLng: LatLng = LatLng(latitude, longitude)
                d.latLng = latLng
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
            return d
        }
    }

    fun getColor(): Int {
        if (this.level < MEDIUM)
            return 0x110000FF
        if (this.level < DANGER)
            return 0x11FFFF00
        return 0x11FF0000
    }
    var title: String = ""
    var id: String = ""
    var notifier: String = ""
    var level: Int = 0
    var radius: Double = 0.0
    var latLng : LatLng? = null
}