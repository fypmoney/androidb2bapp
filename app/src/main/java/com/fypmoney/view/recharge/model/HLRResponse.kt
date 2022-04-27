package com.fypmoney.view.recharge.model

import androidx.annotation.Keep

@Keep
data class HLRResponse(
    val responseCode: Int? = null,
    val message: String? = null,
    val status: String? = null,
    val info: HLRInfo? = null
)

@Keep
data class HLRInfo(
    val circle: String? = null,
    val operator: String? = null
)

