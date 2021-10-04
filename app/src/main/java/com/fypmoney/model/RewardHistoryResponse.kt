package com.fypmoney.model

import androidx.annotation.Keep

@Keep
data class RewardHistoryResponse(
	val isPaid: String? = null,
	val orderNo: String? = null,
	val rewardsProductId: Int? = null,
	val loyaltyPointsBurnt: Int? = null,
	val cashbackWon: Int? = null,
	val isFullfilled: String? = null,
	val descriptionl: String? = null,
	val accountTxnNo: String? = null,
	val rewardProductCode: String? = null
)

