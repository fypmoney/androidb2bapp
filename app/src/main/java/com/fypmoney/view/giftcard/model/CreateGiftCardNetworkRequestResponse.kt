package com.fypmoney.view.giftcard.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class CreateGiftCardBrandNetworkResponse(
	@field:SerializedName("data")
	val data: GiftCardBrandDetails
)

@Keep
data class GiftCardBrandDetails(

	@field:SerializedName("code")
	val code: String? = null,

	@field:SerializedName("displayName")
	val displayName: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("listImage")
	val listImage: String? = null,

	@field:SerializedName("validityInMonths")
	val validityInMonths: String? = null,

	@field:SerializedName("brandTagLine1")
	val brandTagLine1: String? = null,

	@field:SerializedName("brandTagLine2")
	val brandTagLine2: String? = null,

	@field:SerializedName("brandTagLine3")
	val brandTagLine3: Any? = null,

	@field:SerializedName("brandTagLine4")
	val brandTagLine4: Any? = null,

	@field:SerializedName("howToRedeem")
	val howToRedeem: String? = null,

	@field:SerializedName("brandTagLine5")
	val brandTagLine5: Any? = null,

	@field:SerializedName("discountPer")
	val discountPer: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("tncLink")
	val tncLink: String? = null,

	@field:SerializedName("brandLogo")
	val brandLogo: String? = null,

	@field:SerializedName("detailImage")
	val detailImage: String? = null,

	@field:SerializedName("giftMessage")
	val giftMessage: String? = null,

	@field:SerializedName("tnc")
	val tnc: String? = null,

	@field:SerializedName("fixedDenomiation")
	val fixedDenomiation: String? = null,

	@field:SerializedName("voucherProduct")
	val voucherProduct: List<VoucherProduct?>? = null,

	@field:SerializedName("successImage")
	val successImage: String? = null,

	@field:SerializedName("minPrice")
	val minPrice: Int,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("tagline")
	val tagline: String? = null,

	@field:SerializedName("maxPrice")
	val maxPrice: Int,

	@field:SerializedName("possibleDenominationList")
	val possibleDenominationList: String? = null
)

@Keep
data class VoucherProduct(

	@field:SerializedName("amount")
	val amount: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("productGuid")
	val productGuid: String? = null,

	@field:SerializedName("tagline")
	val tagline: String? = null,

	@field:SerializedName("maxAllowedQuantity")
	val maxAllowedQuantity: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("isFlexiblePrice")
	val isFlexiblePrice: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)
