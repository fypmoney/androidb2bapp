package com.fypmoney.view.recharge.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class BillPaymentRequest(
    var amount: String? = null,
    val planType: String? = null,
    var billnetamount: String? = null,
    val latitude: String? = null,
    val dueDate: String? = null,
    val userName: String? = null,
    val cardNo: String? = null,
    val operator: String? = null,
    val acceptPayment: Boolean? = null,
    val cellNumber: String? = null,
    val mode: String? = null,
    var billAmount: Long? = null,
    val billdate: String? = null,
    val acceptPartPay: Boolean? = null,
    var planPrice: String? = null,
    val longitude: String? = null,
    var operatorName:String?=null
) : Parcelable

