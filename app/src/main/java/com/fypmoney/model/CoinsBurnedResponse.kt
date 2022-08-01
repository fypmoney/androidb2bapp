package com.fypmoney.model

import androidx.annotation.Keep

@Keep
data class CoinsBurnedResponse(
    val orderNo: String? = null,
    val loyaltyPointsBurnt: Int? = null,
    val isFullfilled: String? = null,
    val description: String? = null,
    val accountTxnNo: Any? = null,
    val isPaid: String? = null,
    val productCode: Any? = null,
    val rewardsProductId: Any? = null,
    val cashbackWon: Any? = null,
    val loyaltyTxnId: String? = null,
    val rewardProductCode: String? = null,
    val productType: Any? = null,
    val fullfillmentDescription: Any? = null,
    val sectionId: Int? = null,
    val noOfJackpotTicket: Int? = null,
    val sectionCode: String? = null

)

