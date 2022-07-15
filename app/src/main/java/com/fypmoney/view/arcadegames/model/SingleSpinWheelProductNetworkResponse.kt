package com.fypmoney.view.arcadegames.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class SingleSpinWheelProductNetworkResponse(

	@field:SerializedName("data")
	val data: Data? = null
)

@Keep
data class SectionListItem(

	@field:SerializedName("sectionName")
	val sectionName: String? = null,

	@field:SerializedName("sectionValue")
	val sectionValue: String? = null,

	@field:SerializedName("sectionCode")
	val sectionCode: Any? = null,

	@field:SerializedName("colorCode")
	val colorCode: String? = null,

	@field:SerializedName("id")
	val id: String? = null
)

@Keep
data class SpinWheelItem(

	@field:SerializedName("detailResource")
	val detailResource: String? = null,

	@field:SerializedName("appDisplayText")
	val appDisplayText: String? = null,

	@field:SerializedName("backgroundColor")
	val backgroundColor: Any? = null,

	@field:SerializedName("code")
	val code: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("listResource")
	val listResource: String? = null,

	@field:SerializedName("sectionId")
	val sectionId: Any? = null,

	@field:SerializedName("scratchResourceHide")
	val scratchResourceHide: Any? = null,

	@field:SerializedName("scratchResourceShow")
	val scratchResourceShow: Any? = null,

	@field:SerializedName("frequency")
	val frequency: Int? = null,

	@field:SerializedName("sectionValue")
	val sectionValue: Any? = null,

	@field:SerializedName("frequencyPlayed")
	val frequencyPlayed: Int? = null,

	@field:SerializedName("productPoints")
	val productPoints: Any? = null,

	@field:SerializedName("appDisplayTextColor")
	val appDisplayTextColor: Any? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("additionalInfo")
	val additionalInfo: String? = null,

	@field:SerializedName("successResourceId")
	val successResourceId: String? = null,

	@field:SerializedName("sectionList")
	val sectionList: List<SectionListItem?>? = null,

	@field:SerializedName("noOfJackpotTicket")
	val noOfJackpotTicket: Int? = null,

	@field:SerializedName("productType")
	val productType: String? = null,

	@field:SerializedName("rewardTypeOnFailure")
	val rewardTypeOnFailure: String? = null
)

data class Data(
	@field:SerializedName("SPIN_WHEEL")
	val spinWheel: List<SpinWheelItem?>? = null
)
