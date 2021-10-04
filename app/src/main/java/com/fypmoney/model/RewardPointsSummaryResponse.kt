package com.fypmoney.model

import androidx.annotation.Keep

@Keep
data class RewardPointsSummaryResponse(
    val burntPoints: Int? = null,
    val totalPoints: Int? = null,
    val remainingPoints: Int? = null
)

