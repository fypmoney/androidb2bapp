package com.fypmoney.view.recharge.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class PayAndRechargeRequest(
    val amount: Long? = null,
    val planType: String? = null,
    val cardNo: String? = null,
    val planPrice: Long? = null,
    val operator: String? = null
): Parcelable

