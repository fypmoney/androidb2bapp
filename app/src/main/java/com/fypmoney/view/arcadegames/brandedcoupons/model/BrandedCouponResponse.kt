package com.fypmoney.view.arcadegames.brandedcoupons.model

import com.google.gson.annotations.SerializedName

data class BrandedCouponResponse(

	@field:SerializedName("data")
	val data: BrandedData? = null
)

data class COUPONItem(

	@field:SerializedName("detailResource")
	val detailResource: String? = null,

	@field:SerializedName("appDisplayText")
	val appDisplayText: String? = null,

	@field:SerializedName("code")
	val code: String? = null,

	@field:SerializedName("endDate")
	val endDate: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("listResource")
	val listResource: String? = null,

	@field:SerializedName("sectionCode")
	val sectionCode: Any? = null,

	@field:SerializedName("frequency")
	val frequency: Int? = null,

	@field:SerializedName("appDisplayTextColor")
	val appDisplayTextColor: Any? = null,

	@field:SerializedName("additionalInfo")
	val additionalInfo: String? = null,

	@field:SerializedName("successResourceId")
	val successResourceId: String? = null,

	@field:SerializedName("sectionList")
	val sectionList: List<Any?>? = null,

	@field:SerializedName("productType")
	val productType: String? = null,

	@field:SerializedName("backgroundColor")
	val backgroundColor: String? = null,

	@field:SerializedName("leaderBoardLimit")
	val leaderBoardLimit: Int? = null,

	@field:SerializedName("sectionId")
	val sectionId: Any? = null,

	@field:SerializedName("scratchResourceHide")
	val scratchResourceHide: String? = null,

	@field:SerializedName("scratchResourceShow")
	val scratchResourceShow: String? = null,

	@field:SerializedName("rewardTypeOnFailure")
	val rewardTypeOnFailure: String? = null,

	@field:SerializedName("sectionValue")
	val sectionValue: Any? = null,

	@field:SerializedName("frequencyPlayed")
	val frequencyPlayed: Int? = null,

	@field:SerializedName("productPoints")
	val productPoints: Any? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("noOfJackpotTicket")
	val noOfJackpotTicket: Int? = null,

	@field:SerializedName("startDate")
	val startDate: String? = null
)

data class BrandedData(

	@field:SerializedName("COUPON")
	val cOUPON: List<COUPONItem?>? = null
)
