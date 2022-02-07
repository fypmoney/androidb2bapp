package com.fyp.trackr.models

import androidx.annotation.Keep
import com.fyp.trackr.base.Trackr
import com.fyp.trackr.services.TrackrServices
@Keep
data class ScreenEvent(val name: String = "", val services: ArrayList<TrackrServices>, val map: HashMap<String, String> = hashMapOf()) {

    private var dataMap = hashMapOf<String, String>()

    init {
        dataMap = map
    }

    fun mapped() = dataMap

}

// Easy Extensions
fun ScreenEvent?.push() {
    Trackr.s(this)
}