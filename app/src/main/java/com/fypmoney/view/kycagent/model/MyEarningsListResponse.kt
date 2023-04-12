package com.fypmoney.view.kycagent.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class MyEarningsListResponse(

	@field:SerializedName("data")
	val data: MyEarningsData? = null
)

@Keep
data class EarningItem(

	@field:SerializedName("dateTime")
	val dateTime: String? = null,

	@field:SerializedName("earningAmount")
	val earningAmount: Int? = null
)

@Keep
data class MyEarningsData(

	@field:SerializedName("totalEarnings")
	val totalEarnings: String? = null,

	@field:SerializedName("earning")
	val earning: List<EarningItem?>? = null
)
