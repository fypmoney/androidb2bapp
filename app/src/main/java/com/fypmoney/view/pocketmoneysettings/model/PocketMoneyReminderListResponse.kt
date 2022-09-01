package com.fypmoney.view.pocketmoneysettings.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class PocketMoneyReminderListResponse(

	@field:SerializedName("msg")
	val msg: String? = null,

	@field:SerializedName("data")
	val data: List<DataItem?>? = null
)

@Keep
data class DataItem(

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
