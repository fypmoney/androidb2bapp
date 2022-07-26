package com.fypmoney.view.arcadegames.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class MultipleJackpotNetworkResponse(

	@field:SerializedName("data")
	val data: DataJack? = null
)

@Keep
data class JackpotDetailsItem(

	@field:SerializedName("productCode")
	val productCode: String? = null,

	@field:SerializedName("endDate")
	val endDate: String? = null,

	@field:SerializedName("totalJackpotMsg")
	val totalJackpotMsg: String? = null,

	@field:SerializedName("count")
	val count: Int? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("isExpired")
	val isExpired: String,

	@field:SerializedName("listImage")
	val listImage: String,

	@field:SerializedName("startDate")
	val startDate: String? = null,

	@field:SerializedName("productName")
	val productName: String,

	@field:SerializedName("detailImage")
	val detailImage: String? = null
)

@Keep
data class DataJack(

	@field:SerializedName("jackpotDetails")
	val jackpotDetails: List<JackpotDetailsItem?>? = null,

	@field:SerializedName("totalTickets")
	val totalTickets: Int? = null
)
