package com.fypmoney.receivers


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.fypmoney.util.Utility
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status


/**
 * This receiver is used for sms
 */
const val TAG: String = "SmsBroadcastReceiver"

class SmsBroadcastReceiver : BroadcastReceiver() {
    private var otpReceiveInterface: OtpReceivedInterface? = null

    fun setOnOtpListeners(otpReceiveInterface: OtpReceivedInterface) {
        this.otpReceiveInterface = otpReceiveInterface
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "onReceive: ")
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent?.action) {
            val extras: Bundle? = intent.extras
            val mStatus: Status = extras?.get(SmsRetriever.EXTRA_STATUS) as Status

            when (mStatus.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    // Get SMS message contents'
                    val message: String = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String
                    Log.d(TAG, "onReceive$message")
                    Utility.showToast("auto read otp success")

                    if (otpReceiveInterface != null) {
                        //String otp = message.replace("<#> Your otp code is : ", "")
                        val otp: String? = Utility.extractDigits(message)
                        otpReceiveInterface?.onOtpReceived(otp)
                    }
                }
                CommonStatusCodes.TIMEOUT -> {// Waiting for SMS timed out (5 minutes)
                    Log.d(TAG, "onReceive: failure")
                    if (otpReceiveInterface != null) {
                        otpReceiveInterface?.onOtpTimeout()
                        Utility.showToast("auto read otp timeout")
                    }
                }

            }
        }
    }
}

interface OtpReceivedInterface {
    fun onOtpReceived(otp: String?)
    fun onOtpTimeout()
}