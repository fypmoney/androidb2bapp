package com.fypmoney.view.kycagent.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class FetchShopDetailsResponse(

	@field:SerializedName("data")
	val data: ShopData? = null
)

@Keep
data class ShopData(

	@field:SerializedName("pincode")
	val pincode: Int? = null,

	@field:SerializedName("agentContact1")
	val agentContact1: String? = null,

	@field:SerializedName("addr2")
	val addr2: String? = null,

	@field:SerializedName("agentContact2")
	val agentContact2: String? = null,

	@field:SerializedName("addr1")
	val addr1: String? = null,

	@field:SerializedName("city")
	val city: String? = null,

	@field:SerializedName("addr3")
	val addr3: String? = null,

	@field:SerializedName("latitude")
	val latitude: String? = null,

	@field:SerializedName("agentName")
	val agentName: String? = null,

	@field:SerializedName("shopName")
	val shopName: String? = null,

	@field:SerializedName("gstNumber")
	val gstNumber: String? = null,

	@field:SerializedName("isPosterOrdered")
	val isPosterOrdered: String? = null,

	@field:SerializedName("legalBusinessName")
	val legalBusinessName: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("state")
	val state: String? = null,

	@field:SerializedName("isShopListed")
	val isShopListed: Any? = null,

	@field:SerializedName("shopPhoto")
	val shopPhoto: String? = null,

	@field:SerializedName("longitude")
	val longitude: String? = null
)
