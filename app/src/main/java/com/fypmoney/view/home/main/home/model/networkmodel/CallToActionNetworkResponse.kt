package com.fypmoney.view.home.main.home.model.networkmodel

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class CallToActionNetworkResponse(

	@field:SerializedName("data")
	val data: List<DataItem?>? = null
)
@Keep
data class SectionContentItem(

	@field:SerializedName("contentResourceUri")
	val contentResourceUri: String? = null,

	@field:SerializedName("redirectionResource")
	val redirectionResource: String? = null,

	@field:SerializedName("redirectionType")
	val redirectionType: String? = null,

	@field:SerializedName("sortOrder")
	val sortOrder: Int? = null,

	@field:SerializedName("contentDimensionX")
	val contentDimensionX: Int? = null,

	@field:SerializedName("actionFlagCode")
	val actionFlagCode: String? = null,

	@field:SerializedName("id")
	val id: Int = 0,

	@field:SerializedName("contentType")
	val contentType: String? = null,

	@field:SerializedName("contentDimensionY")
	val contentDimensionY: Int? = null,

	@field:SerializedName("status")
	val status: String? = null
)
@Keep
data class DataItem(

	@field:SerializedName("sectionSortOrder")
	val sectionSortOrder: Int? = null,

	@field:SerializedName("actionCardFlag")
	val actionCardFlag: String? = null,

	@field:SerializedName("redirectionType")
	val redirectionType: String? = null,

	@field:SerializedName("sectionContent")
	val sectionContent: List<SectionContentItem?>? = null,

	@field:SerializedName("showMore")
	val showMore: String? = null,

	@field:SerializedName("showMoreRedirectionResource")
	val showMoreRedirectionResource: String? = null,

	@field:SerializedName("sectionCode")
	val sectionCode: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("sectionDisplayText")
	val sectionDisplayText: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)
