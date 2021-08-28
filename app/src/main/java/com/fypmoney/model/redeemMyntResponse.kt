package com.fypmoney.model

import androidx.annotation.Keep

@Keep
data class RedeemMyntResponse(
    val displayCard: String? = null,
    val totalPoints: Int? = null,
    val description: Any? = null,
    val pointsToRedeem: Int? = null,
    val tncLink: String? = null,
    val alreadyRedeemed: String? = null
)

