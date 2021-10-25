package com.fypmoney.model

import androidx.annotation.Keep

@Keep
data class RewardPointsSummaryResponse(
    val burntPoints: Float? = null,
    val totalPoints: Float? = null,
    val remainingPoints: Float? = null
)

