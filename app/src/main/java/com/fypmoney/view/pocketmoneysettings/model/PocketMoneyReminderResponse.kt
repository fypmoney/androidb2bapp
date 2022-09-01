package com.fypmoney.view.pocketmoneysettings.model

import com.google.gson.annotations.SerializedName

data class PocketMoneyReminderResponse(

	@field:SerializedName("msg")
	val msg: String? = null,

	@field:SerializedName("data")
	val data: Data? = null
)

data class Data(

	@field:SerializedName("amount")
	val amount: Int? = null,

	@field:SerializedName("otpIdentifier")
	val otpIdentifier: String? = null,

	@field:SerializedName("mobile")
	val mobile: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("identifierType")
	val identifierType: String? = null,

	@field:SerializedName("relation")
	val relation: String? = null,

	@field:SerializedName("frequency")
	val frequency: String? = null
)
