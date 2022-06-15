package com.fypmoney.view.giftcard.model

data class GiftHistoryResponse(
	val amount: Int? = null,
	val sourceUserName: String? = null,
	val endDate: String? = null,
	val tnc: String? = null,
	val productGuid: String? = null,
	val destinationEmail: Any? = null,
	val voucherGcCode: String? = null,
	val externalOrderId: String? = null,
	val message: String? = null,
	val productName: String? = null,
	val voucherGuid: String? = null,
	val voucherNo: String? = null,
	val destinationUserName: String? = null,
	val destinationMobileNo: String? = null,
	val voucherName: String? = null,
	val id: Int? = null,
	val sourceUserId: Int? = null,
	val issueDate: String? = null,
	val tncLink: Any? = null,
	val sourceMobileNo: String? = null,
	val isGifted: String? = null,
	val giftedPerson: String? = null,
	val destinationUserId: Int? = null,
	val detailImage: String? = null
)

