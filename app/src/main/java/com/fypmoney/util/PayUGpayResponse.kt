package com.fypmoney.util

import android.util.Log
import com.payu.gpay.callbacks.PayUGPayCallback

class PayUGpayResponse(var onGpayResponseListener: OnGpayResponseListener) : PayUGPayCallback() {

    override fun onPaymentSuccess(payuResponse: String?, merchantResponse: String?) {
        super.onPaymentSuccess(payuResponse, merchantResponse)
        onGpayResponseListener.onGpayResponseListener(payuResponse)
    }

    override fun onPaymentFailure(payuResponse: String?, merchantResponse: String?) {
        super.onPaymentFailure(payuResponse, merchantResponse)
    }

    override fun onGpayErrorReceived(errorCode: Int, description: String?) {
        super.onGpayErrorReceived(errorCode, description)
        Log.d("gpay_error", errorCode.toString() + description)
    }

    override fun onPaymentInitialisationFailure(errorCode: Int, description: String?) {
        super.onPaymentInitialisationFailure(errorCode, description)
        Log.d("gpay_error_initialise", errorCode.toString() + description)

    }

    override fun onPaymentInitialisationSuccess() {
        super.onPaymentInitialisationSuccess()
        Log.d("gpay_success_initializn", "success")

    }

    interface OnGpayResponseListener {
        fun onGpayResponseListener(payuResponse: String?)
    }


}