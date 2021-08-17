package com.fypmoney.model

import com.google.gson.annotations.SerializedName

data class RedeemRequestedResponse(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("tncLink")
	val tncLink: String? = null
)
