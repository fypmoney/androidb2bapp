package com.fypmoney.viewhelper

import android.util.Log
import androidx.databinding.ObservableField
import com.fypmoney.model.GetRewardsHistoryResponseDetails
import java.text.SimpleDateFormat
import java.util.*

class RewardsHistoryViewHelper(var rewardsHistory: GetRewardsHistoryResponseDetails?) {
    var coins = ObservableField<String>()
    var date = ObservableField<String>()
    var isLineVisible = ObservableField<Boolean>()

    init {

        if ((rewardsHistory?.sectionValue) == "0") {
            coins.set("Oops! No Fyp Mynts")

        } else {
            coins.set(((rewardsHistory?.sectionValue)?.toInt()!! / 100).toString() + " Fyp Mynts")

        }

//        val sf = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.m'Z'",Locale.US)
//        val outDate = SimpleDateFormat("MMM dd' ,'hh:mm a",Locale.US)
////        sf.timeZone = TimeZone.getTimeZone("IST")
//        val dtt = sf.parse( rewardsHistory?.playedOn)

        try {
            val sf = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'", Locale.US)
            val outDate = SimpleDateFormat("MMM dd' ,'hh:mm a", Locale.US)
            sf.timeZone = TimeZone.getTimeZone("IST")

            Log.d("chack", rewardsHistory?.playedOn.toString())
            val dtt = sf.parse(rewardsHistory?.playedOn)

            println(outDate.format(dtt))
            date.set(
                "Won on " + outDate.format(dtt)
            )
        } catch (e: Exception) {

        }


    }
}