package com.fypmoney.view.whatsappnoti.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class WhatsAppNotificationResponse(

	@field:SerializedName("data")
	val data: WhatsAppOptData? = null
)

@Keep
data class WhatsAppOptData(

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
