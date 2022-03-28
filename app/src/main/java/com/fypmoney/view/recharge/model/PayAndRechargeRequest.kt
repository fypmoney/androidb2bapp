package com.fypmoney.view.recharge.model

data class PayAndRechargeRequest(
	val amount: Long? = null,
	val planType: String? = null,
	val cardNo: String? = null,
	val planPrice: Long? = null,
	val operator: String? = null
)

