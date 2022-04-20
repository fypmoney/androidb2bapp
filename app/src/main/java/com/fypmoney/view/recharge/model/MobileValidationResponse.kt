package com.fypmoney.view.recharge.model

import androidx.annotation.Keep

@Keep
data class MobileValidationResponse(
    val responseCode: Int? = null,
    val message: String? = null,
    val status: String? = null,
    val info: Info? = null
)

@Keep
data class Info(
    val circle: String? = null,
    val operator: String? = null
)

