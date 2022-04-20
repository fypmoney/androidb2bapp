package com.fypmoney.view.recharge.model

import androidx.annotation.Keep

@Keep
data class PayAndRechargeRequest(
    val amount: Long? = null,
    val planType: String? = null,
    val cardNo: String? = null,
    val planPrice: Long? = null,
    val operator: String? = null
)

