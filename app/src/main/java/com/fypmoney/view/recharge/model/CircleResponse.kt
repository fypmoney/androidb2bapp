package com.fypmoney.view.recharge.model

import androidx.annotation.Keep

@Keep
data class CircleResponse(
    val code: String? = null,
    val name: String? = null,
    val id: String? = null,
    val displayName: String? = null,
    val operatorId: String? = null,
    val status: String? = null
)

