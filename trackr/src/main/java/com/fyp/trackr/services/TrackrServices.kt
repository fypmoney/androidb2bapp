package com.fyp.trackr.services

enum class TrackrServices {
    MOENGAGE, FIREBASE,ADJUST,FB
}
//Todo needs to handel at one place
var listOfServices:List<TrackrServices> = arrayListOf(
    TrackrServices.FIREBASE,
    TrackrServices.MOENGAGE,
    TrackrServices.FB,TrackrServices.ADJUST
)