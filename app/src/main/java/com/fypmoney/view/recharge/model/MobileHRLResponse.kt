package com.fypmoney.view.recharge.model

data class MobileHRLResponse(
    val responseCode: Int? = null,
    val message: String? = null,
    val status: Boolean? = null,
    val info: List<InfoItem?>? = null
)

data class InfoItem(
    val nextRechargeDate: String? = null,
    val planName: Any? = null,
    val monthlyRecharge: Int? = null,
    val balance: Double? = null,
    val customerName: String? = null,
    val status: String? = null
)

