package com.fypmoney.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class RedeemRequestedResponse(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("tncLink")
	val tncLink: String? = null
)
