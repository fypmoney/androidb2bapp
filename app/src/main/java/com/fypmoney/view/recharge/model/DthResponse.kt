package com.fypmoney.view.recharge.model

import androidx.annotation.Keep

@Keep
data class DthResponse(
    val response_code: Int? = null,
    val message: String? = null,
    val status: Boolean? = null,
    val info: List<InfoItem2?>? = null
)

@Keep
data class InfoItem2(
    val NextRechargeDate: String? = null,
    val planName: Any? = null,
    val MonthlyRecharge: Int? = null,
    val Balance: Double? = null,
    val customerName: String? = null,
    val status: String? = null
)

