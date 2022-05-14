package com.fypmoney.view.recharge.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class PayAndRechargeResponse(
    val orderType: String? = null,
    val planType: String? = null,
    val orderNo: String? = null,
    val planName: String? = null,
    val description: String? = null,
    val mobileNo: String? = null,
    val cardNo: String? = null,
    val isPurchased: String? = null,
    val uaaId: String? = null,
    val planCode: String? = null,
    val reversalTxnNo: String? = null,
    val merchantResponseMsg: String? = null,
    val paymentIdentifier: String? = null,
    val merchantResponseCode: String? = null,
    val candidateForReversal: String? = null,
    val isFullFilled: String? = null,
    val currencyCode: String? = null,
    val planPrice: Int? = null,
    val operatorId: String? = null,
    val paymentStatus: String? = null,
    val merchantHttpCode: String? = null,
    val status: String? = null,
    val isReversed: String? = null
) : Parcelable

