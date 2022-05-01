package com.fypmoney.view.recharge.model

import androidx.annotation.Keep

@Keep
data class FetchbillRequest(
    val mode: String? = null,
    val operator: String? = null,
    val canumber: String? = null
)

