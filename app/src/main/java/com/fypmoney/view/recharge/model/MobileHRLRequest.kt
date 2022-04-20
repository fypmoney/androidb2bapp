package com.fypmoney.view.recharge.model

import androidx.annotation.Keep

@Keep
data class MobileHRLRequest(
    val mobile: String? = null,
    val type: String? = null
)

