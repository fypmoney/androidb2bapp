package com.fypmoney.view.arcadegames.model

import androidx.annotation.Keep

@Keep
data class MultipleJackpotResponse(
    val productImage: String? = null,
    var productName: String? = null,
    var jackpotDuration: String? = null,
    var jackpotAmount: String? = null,
    var jackpotExpired: String? = null
)