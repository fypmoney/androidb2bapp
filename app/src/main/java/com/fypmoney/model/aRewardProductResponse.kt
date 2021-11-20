package com.fypmoney.model

import androidx.annotation.Keep

@Keep
data class aRewardProductResponse(
    val detailResource: String? = null,
    val appDisplayText: String? = null,
    val code: String? = null,
    val productPoints: Any? = null,
    val name: String? = null,
    val description: String? = null,
    val listResource: Any? = null,
    val successResourceId: Any? = null,
    val scratchResourceHide: String? = null,
    val sectionList: List<SectionListItem?>? = null,
    val scratchResourceShow: String? = null,
    val productType: String? = null,
    val sectionId: Int? = null,
    val appDisplayTextColor: String? = null,
    val backgroundColor: String? = null,
    val noOfJackpotTicket: Int? = null


    )

@Keep
data class SectionListItem(
    val sectionName: String? = null,
    val sectionValue: String? = null,
    val colorCode: String? = null,
    val id: String? = null
)

