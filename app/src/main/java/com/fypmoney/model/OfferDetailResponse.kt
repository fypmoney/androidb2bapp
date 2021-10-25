package com.fypmoney.model

data class OfferDetailResponse(
	val data: Data2? = null
)

data class GetAllFeed(
	val feedData: List<FeedDataItem?>? = null,
	val total: Int? = null
)

data class Offers(
	val date: String? = null,
	val logoImg: String? = null,
	val code: String? = null,
	val innerBannerImg: String? = null,
	val tnc: String? = null,
	val details: List<String?>? = null,
	val title: String? = null,
)
data class OfferAction(
	var url:String? = null
)
data class Data2(
	val getAllFeed: GetAllFeed? = null
)

data class FeedDataItem(
	val offers: Offers? = null,
	val action: OfferAction? = null

)

