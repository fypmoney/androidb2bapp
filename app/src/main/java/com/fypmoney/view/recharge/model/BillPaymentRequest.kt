package com.fypmoney.view.recharge.model

import androidx.annotation.Keep

@Keep
data class BillPaymentRequest(
    val amount: Double? = null,
    val planType: String? = null,
    val billnetamount: Double? = null,
    val latitude: String? = null,
    val dueDate: String? = null,
    val userName: String? = null,
    val cardNo: String? = null,
    val operator: Int? = null,
    val acceptPayment: Boolean? = null,
    val cellNumber: String? = null,
    val mode: String? = null,
    val billAmount: Double? = null,
    val billdate: String? = null,
    val acceptPartPay: Boolean? = null,
    val planPrice: Double? = null,
    val longitude: String? = null
)

