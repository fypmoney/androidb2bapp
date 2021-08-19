package com.fypmoney.viewhelper

import android.util.Log
import androidx.databinding.ObservableField
import com.fypmoney.model.GetRewardsHistoryResponseDetails
import java.text.SimpleDateFormat
import java.util.*

class RewardsHistoryViewHelper(var rewardsHistory: GetRewardsHistoryResponseDetails?) {
    var coins = ObservableField<String>()
    var date = ObservableField<String>()
    var credit = ObservableField<Boolean>()

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
            if (rewardsHistory!!.rewardTxnType == "CREDIT") {
                credit.set(false)

                date.set(
                    "Won on " + outDate.format(dtt)
                )
            } else {
                date.set(
                    "Credited on " + outDate.format(dtt)
                )
                credit.set(true)

            }


        } catch (e: Exception) {

        }


    }
}