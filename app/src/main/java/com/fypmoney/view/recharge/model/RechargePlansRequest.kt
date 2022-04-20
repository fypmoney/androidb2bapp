package com.fypmoney.view.recharge.model

import androidx.annotation.Keep

@Keep
data class RechargePlansRequest(
    val circle: String? = null,
    val operator: String? = null
)

