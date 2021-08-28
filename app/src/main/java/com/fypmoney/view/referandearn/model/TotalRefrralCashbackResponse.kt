package com.fypmoney.view.referandearn.model

import androidx.annotation.Keep
@Keep
data class TotalRefrralCashbackResponse(
    val data: ReferralData
)

@Keep
data class ReferralData(
    val totalAmount: Int
)