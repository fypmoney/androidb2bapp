package com.fypmoney.view.arcadegames.brandedcoupons.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class BrandedCouponResponse(

    @field:SerializedName("data")
    val data: BrandedData? = null
)

@Keep
data class SectionListItem(

    @field:SerializedName("sectionName")
    val sectionName: String? = null,

    @field:SerializedName("sectionValue")
    val sectionValue: String? = null,

    @field:SerializedName("sectionCode")
    val sectionCode: String? = null,

    @field:SerializedName("colorCode")
    val colorCode: String? = null,

    @field:SerializedName("id")
    val id: Int? = null
)

@Keep
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

    @field:SerializedName("isCouponAssigned")
    val isCouponAssigned: String? = null,

    @field:SerializedName("appDisplayTextColor")
    val appDisplayTextColor: Any? = null,

    @field:SerializedName("additionalInfo")
    val additionalInfo: String? = null,

    @field:SerializedName("successResourceId")
    val successResourceId: String? = null,

    @field:SerializedName("sectionList")
    val sectionList: List<SectionListItem?>? = null,

    @field:SerializedName("productType")
    val productType: String? = null,

    @field:SerializedName("backgroundColor")
    val backgroundColor: String? = null,

    @field:SerializedName("couponDetails")
    val couponDetails: CouponDetails? = null,

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

    @field:SerializedName("rewardColor")
    val rewardColor: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("noOfJackpotTicket")
    val noOfJackpotTicket: Int? = null,

    @field:SerializedName("startDate")
    val startDate: String? = null
)



@Keep
data class BrandedData(

    @field:SerializedName("COUPON")
    val cOUPON: List<COUPONItem?>? = null
)
