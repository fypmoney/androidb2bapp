package com.fypmoney.model

import androidx.annotation.Keep

@Keep
data class OfferDetailResponse(
	val data: Data2? = null
)

@Keep
data class GetAllFeed(
	val feedData: List<FeedDataItem?>? = null,
	val total: Int? = null
)
@Keep
data class Offers(
	val date: String? = null,
	val logoImg: String? = null,
	val code: String? = null,
	val innerBannerImg: String? = null,
	val tnc: String? = null,
	val details: List<String?>? = null,
	val title: String? = null,
)
@Keep
data class OfferAction(
	var url:String? = null
)
@Keep
data class Data2(
	val getAllFeed: GetAllFeed? = null
)
@Keep
data class FeedDataItem(
	val offers: Offers? = null,
	val action: OfferAction? = null

)

