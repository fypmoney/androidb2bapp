package com.fypmoney.view.giftcard.model

import androidx.annotation.Keep

@Keep
data class GiftDetailsResponse(
	val endDate: String? = null,
	val productGuid: String? = null,
	val destinationEmail: Any? = null,
	val voucherGcCode: Any? = null,
	val giftVoucherOrderNo: String? = null,
	val productName: String? = null,
	val destinationUserName: String? = null,
	val id: Int? = null,
	val issueDate: String? = null,
	val tncLink: String? = null,
	val sourceMobileNo: String? = null,
	val giftedPerson: String? = null,
	val destinationUserId: Int? = null,
	val detailImage: String? = null,
	val amount: Int? = null,
	val sourceUserName: String? = null,
	val rfu2: Any? = null,
	val rfu1: Any? = null,
	val rfu3: Any? = null,
	val tnc: String? = null,
	val externalOrderId: String? = null,
	val isVoucherPurchased: String? = null,
	val message: String? = null,
	val voucherGuid: String? = null,
	val voucherNo: String? = null,
	val destinationMobileNo: String? = null,
	val voucherName: String? = null,
	val voucherStatus: String? = null,
	val sourceUserId: Int? = null,
	val isGifted: String? = null
)

