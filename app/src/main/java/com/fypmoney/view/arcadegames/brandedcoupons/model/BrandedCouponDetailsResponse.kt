package com.fypmoney.view.arcadegames.brandedcoupons.model

import com.google.gson.annotations.SerializedName

data class BrandedCouponDetailsResponse(

	@field:SerializedName("data")
	val data: CouponDetailsData? = null
)

data class CouponDetailsData(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("brandName")
	val brandName: String? = null,

	@field:SerializedName("code")
	val code: String? = null,

	@field:SerializedName("howToRedeem")
	val howToRedeem: String? = null,

	@field:SerializedName("tnc")
	val tnc: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("entityId")
	val entityId: String? = null,

	@field:SerializedName("redeemLink")
	val redeemLink: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("tncLink")
	val tncLink: String? = null,

	@field:SerializedName("brandLogo")
	val brandLogo: String? = null
)
