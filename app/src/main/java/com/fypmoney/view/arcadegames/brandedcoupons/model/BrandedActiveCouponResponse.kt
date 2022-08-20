package com.fypmoney.view.arcadegames.brandedcoupons.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class BrandedActiveCouponResponse(

	@field:SerializedName("data")
	val data: BrandedActiveCouponData? = null
)

@Keep
data class ActiveCouponsListItem(

	@field:SerializedName("expiry")
	val expiry: String? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("brandLogo")
	val brandLogo: String? = null,

	@field:SerializedName("couponCode")
	val couponCode: String? = null
)

@Keep
data class BrandedActiveCouponData(

	@field:SerializedName("activeCouponsList")
	val activeCouponsList: List<ActiveCouponsListItem?>? = null
)
