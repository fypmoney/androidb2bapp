package com.fypmoney.view.arcadegames.brandedcoupons.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class BrandedCouponCountResponse(

	@field:SerializedName("data")
	val data: Data? = null
)

@Keep
data class Data(

	@field:SerializedName("amount")
	val amount: Int? = null
)
