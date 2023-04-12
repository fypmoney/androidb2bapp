package com.fypmoney.view.kycagent.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class OtpVerifyResponse(
	@field:SerializedName("data")
	val data: String? = null
)
