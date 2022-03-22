package com.fypmoney.model

import androidx.annotation.Keep

@Keep
data class CashbackWonResponse(
	val transactionType: String? = null,
	val bankReferenceNumber: String? = null,
	val amount: String? = null,
	val paymentMode: String? = null,
	val accReferenceNumber: String? = null,
	val mrn: Any? = null,
	val mobileNo: Any? = null,
	val message: String? = null,
	val transactionDate: String? = null,
	val userName: String? = null
)

