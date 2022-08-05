package com.fypmoney.view.insights.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Keep
data class SpendAndIncomeNetworkResponse(

	@field:SerializedName("income")
	val income: Income? = null,

	@field:SerializedName("allTxn")
	val allTxn: List<AllTxnItem?>? = null,

	@field:SerializedName("spent")
	val spent: Spent? = null
)

@Keep
data class Spent(

	@field:SerializedName("total")
	val total: String? = null,

	@field:SerializedName("category")
	val category: List<CategoryItem?>? = null
)

@Keep
data class CategoryItem(

	@field:SerializedName("amount")
	val amount: Double? = null,

	@field:SerializedName("percentage")
	val percentage: Double? = null,

	@field:SerializedName("categoryCol")
	val categoryCol: String? = null,

	@field:SerializedName("categoryCode")
	val categoryCode: String? = null,

	@field:SerializedName("category")
	val category: String? = null,

	@field:SerializedName("icon_link")
	val iconLink: String? = null
)

@Parcelize
@Keep
data class AllTxnItem(

	@field:SerializedName("amount")
	var amount: String? = null,

	@field:SerializedName("paymentMode")
	var paymentMode: String? = null,

	@field:SerializedName("accReferenceNumber")
	var accReferenceNumber: String? = null,

	@field:SerializedName("mrn")
	var mrn: String? = null,

	@field:SerializedName("mobileNo")
	var mobileNo: String? = null,

	@field:SerializedName("categoryCode")
	var categoryCode: String? = null,

	@field:SerializedName("message")
	var message: String? = null,

	@field:SerializedName("userName")
	var userName: String? = null,

	@field:SerializedName("transactionDate")
	var transactionDate: String? = null,

	@field:SerializedName("transactionType")
	var transactionType: String? = null,

	@field:SerializedName("bankReferenceNumber")
	var bankReferenceNumber: String? = null,

	@field:SerializedName("iconLink")
	var iconLink: String? = null,

	@field:SerializedName("category")
	var category: String? = null
):Parcelable

@Keep
data class Income(

	@field:SerializedName("total")
	val total: String? = null,

	@field:SerializedName("category")
	val category: List<CategoryItem?>? = null
)
