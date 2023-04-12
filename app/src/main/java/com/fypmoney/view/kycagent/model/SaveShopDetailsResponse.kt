package com.fypmoney.view.kycagent.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class SaveShopDetailsResponse(

	@field:SerializedName("msg")
	val msg: String? = null
)
