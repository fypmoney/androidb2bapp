package com.fypmoney.view.pocketmoneysettings.model

import com.google.gson.annotations.SerializedName

data class PocketMoneyOtpVerifyResponse(

	@field:SerializedName("msg")
	val msg: String? = null,

	@field:SerializedName("data")
	val data: OtpVerifyData? = null
)

data class OtpVerifyData(

	@field:SerializedName("isOptInDone")
	val isOptInDone: String? = null,

	@field:SerializedName("optInStatus")
	val optInStatus: String? = null,

	@field:SerializedName("uaaId")
	val uaaId: Int? = null,

	@field:SerializedName("uniqueId")
	val uniqueId: String? = null,

	@field:SerializedName("token")
	val token: Any? = null
)
