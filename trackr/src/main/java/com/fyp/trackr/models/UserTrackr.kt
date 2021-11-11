package com.fyp.trackr.models

import android.location.Location
import com.fyp.trackr.base.Trackr

object UserTrackr {



}
fun UserTrackr.setMoengage(){

}
fun UserTrackr.push(map: HashMap<String, Any>) {
    Trackr.data(map)
}

fun UserTrackr.pushLocation(location: Location?) {
    Trackr.location(location)
}


fun UserTrackr.loginWithMap(map: HashMap<String, Any>) {
    Trackr.login(map)
}

fun UserTrackr.login(userId: String) {
    Trackr.login(userId)
}
fun UserTrackr.logOut() {
    Trackr.logOut()
}

fun UserTrackr.setDateOfBirthDate(dob:String) {
    Trackr.sendDob(dob)
}