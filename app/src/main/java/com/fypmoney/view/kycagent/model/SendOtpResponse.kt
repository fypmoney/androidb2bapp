package com.fypmoney.view.kycagent.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class SendOtpResponse(

	@field:SerializedName("msg")
	val msg: String? = null,

	@field:SerializedName("data")
	val data: OtpData? = null
)

@Keep
data class OtpData(

	@field:SerializedName("otpIdentifier")
	val otpIdentifier: String? = null
)
