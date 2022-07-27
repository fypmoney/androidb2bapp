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
	val amount: String? = null,

	@field:SerializedName("paymentMode")
	val paymentMode: String? = null,

	@field:SerializedName("accReferenceNumber")
	val accReferenceNumber: String? = null,

	@field:SerializedName("mrn")
	val mrn: String? = null,

	@field:SerializedName("mobileNo")
	val mobileNo: String? = null,

	@field:SerializedName("categoryCode")
	val categoryCode: String? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("userName")
	val userName: String? = null,

	@field:SerializedName("transactionDate")
	val transactionDate: String? = null,

	@field:SerializedName("transactionType")
	val transactionType: String? = null,

	@field:SerializedName("bankReferenceNumber")
	val bankReferenceNumber: String? = null,

	@field:SerializedName("iconLink")
	val iconLink: String? = null,

	@field:SerializedName("category")
	val category: String? = null
):Parcelable

@Keep
data class Income(

	@field:SerializedName("total")
	val total: String? = null,

	@field:SerializedName("category")
	val category: List<CategoryItem?>? = null
)
