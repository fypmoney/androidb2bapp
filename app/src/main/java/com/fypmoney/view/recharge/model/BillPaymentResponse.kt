package com.fypmoney.view.recharge.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class BillPaymentResponse(
    val orderType: String? = null,
    val latitude: String? = null,
    val dueDate: String? = null,
    val planName: String? = null,
    val description: String? = null,
    val cardNo: String? = null,
    val isPurchased: String? = null,
    val uaaId: String? = null,
    val reversalTxnNo: String? = null,
    val acceptPayment: String? = null,
    val cellNumber: String? = null,
    val mode: String? = null,
    val billNetAmount: Double? = null,
    val billAmount: Double? = null,
    val merchantResponseCode: String? = null,
    val candidateForReversal: String? = null,
    val isFullFilled: String? = null,
    val planPrice: Int? = null,
    val operatorId: String? = null,
    val paymentStatus: String? = null,
    val merchantHttpCode: String? = null,
    val longitude: String? = null,
    val planType: String? = null,
    val orderNo: String? = null,
    val mobileNo: String? = null,
    val userName: String? = null,
    val planCode: String? = null,
    val merchantResponseMsg: String? = null,
    val paymentIdentifier: String? = null,
    val billdate: String? = null,
    val acceptPartPay: String? = null,
    val currencyCode: String? = null,
    val status: String? = null,
    val isReversed: String? = null,


    ) : Parcelable

