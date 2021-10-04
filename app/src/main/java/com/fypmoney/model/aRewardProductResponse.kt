package com.fypmoney.model

import androidx.annotation.Keep

@Keep
data class aRewardProductResponse(
	val detailResource: Any? = null,
	val appDisplayText: String? = null,
	val code: String? = null,
	val productPoints: Any? = null,
	val name: String? = null,
	val description: String? = null,
	val listResource: Any? = null,
	val successResourceId: Any? = null,
	val scratchResourceHide: Any? = null,
	val sectionList: List<SectionListItem?>? = null,
	val scratchResourceShow: Any? = null,
	val productType: String? = null
)

@Keep
data class SectionListItem(
	val sectionName: String? = null,
	val sectionValue: String? = null,
	val colorCode: String? = null
)

