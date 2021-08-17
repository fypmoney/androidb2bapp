package com.fypmoney.model

import com.google.gson.annotations.SerializedName

data class RedeemDetailsResponse(

	@field:SerializedName("displayCard")
	val displayCard: String? = null,

	@field:SerializedName("totalPoints")
	val totalPoints: Int? = null,

	@field:SerializedName("description")
	val description: Any? = null,

	@field:SerializedName("pointsToRedeem")
	val pointsToRedeem: Int? = null,

	@field:SerializedName("tncLink")
	val tncLink: String? = null,

	@field:SerializedName("alreadyRedeemed")
	val alreadyRedeemed: String? = null,

	@field:SerializedName("message")
	val message: String? = null,


	)
