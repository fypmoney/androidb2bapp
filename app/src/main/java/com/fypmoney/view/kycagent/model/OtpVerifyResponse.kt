package com.fypmoney.view.kycagent.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class OtpVerifyResponse(

	@field:SerializedName("msg")
	val msg: String? = null,

	@field:SerializedName("data")
	val data: OtpVerifyData? = null
)

@Keep
data class OtpVerifyData(

	@field:SerializedName("aadhaarDigits")
	val aadhaarDigits: String? = null
)
