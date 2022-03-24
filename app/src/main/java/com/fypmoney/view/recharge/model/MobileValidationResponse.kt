package com.fypmoney.view.recharge.model

data class MobileValidationResponse(
    val responseCode: Int? = null,
    val message: String? = null,
    val status: String? = null,
    val info: Info? = null
)

data class Info(
    val circle: String? = null,
    val operator: String? = null
)

