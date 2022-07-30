package com.fypmoney.view.insights.model

import com.google.gson.annotations.SerializedName

data class UpdatedCategoryResponse(

	@field:SerializedName("data")
	val data: Data? = null
)

data class Data(

	@field:SerializedName("amount")
	val amount: Any? = null,

	@field:SerializedName("paymentMode")
	val paymentMode: Any? = null,

	@field:SerializedName("accReferenceNumber")
	val accReferenceNumber: String? = null,

	@field:SerializedName("mrn")
	val mrn: Any? = null,

	@field:SerializedName("mobileNo")
	val mobileNo: Any? = null,

	@field:SerializedName("categoryCode")
	val categoryCode: String? = null,

	@field:SerializedName("message")
	val message: Any? = null,

	@field:SerializedName("transactionDate")
	val transactionDate: Any? = null,

	@field:SerializedName("userName")
	val userName: Any? = null,

	@field:SerializedName("transactionType")
	val transactionType: Any? = null,

	@field:SerializedName("bankReferenceNumber")
	val bankReferenceNumber: Any? = null,

	@field:SerializedName("iconLink")
	val iconLink: String? = null,

	@field:SerializedName("category")
	val category: String? = null
)
