package com.fypmoney.view.giftCardModule.model

data class GiftSearchResponse(
	val code: String? = null,
	val name: String? = null,
	val description: String? = null,
	val voucherBrand: List<VoucherBrandItem?>? = null,
	val listImage: Any? = null,
	val status: String? = null
)

data class VoucherBrandItem(
	val code: String? = null,
	val giftMessage: String? = null,
	val displayName: String? = null,
	val tnc: String? = null,
	val description: String? = null,
	val listImage: String? = null,
	val validityInMonths: String? = null,
	val name: String? = null,
	val tagline: String? = null,
	val discountPer: Int? = null,
	val id: Int? = null,
	val tncLink: Any? = null,
	val detailImage: String? = null
)

