package com.fypmoney.receivers

import android.content.Context
import android.content.IntentFilter
import android.util.Log
import com.google.android.gms.auth.api.phone.SmsRetriever

class AutoReadOtpUtils(
    private val context: Context
) : OtpSMSBroadcastReceiver.OTPReceiveListener {
    private val TAG = AutoReadOtpUtils::class.java.simpleName
    private var otpReceivedHandler: ((String) -> Unit)? = null
    private val smsBroadcastReceiver = OtpSMSBroadcastReceiver().apply {
        initOTPListener(this@AutoReadOtpUtils)
    }

    fun initialise() {
        startSmsRetriever()
        registerSmsBroadcastReceiver()
    }

    private fun startSmsRetriever() {
        val client = SmsRetriever.getClient(context)
        val task = client.startSmsRetriever()
        task.addOnSuccessListener {
            Log.d(TAG,"SMS Retriever starts")
        }

        task.addOnFailureListener {
            Log.e(TAG,"Cannot Start SMS Retriever")
        }
    }

    private fun registerSmsBroadcastReceiver() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
        context.registerReceiver(smsBroadcastReceiver, intentFilter)
    }

    override fun onOTPReceived(otp: String) {
        otpReceivedHandler?.invoke(otp)
        terminate()
    }

    override fun onOTPTimeOut() {
        Log.d(TAG,"SMS Retriever starts")
        terminate()
    }

    private fun terminate() {
        context.unregisterReceiver(smsBroadcastReceiver)
    }

    fun registerOtpReceiver(handler: (String) -> Unit) {
        this.otpReceivedHandler = handler
    }

    fun unregisterOtpReceiver() {
        this.otpReceivedHandler = null
    }
}