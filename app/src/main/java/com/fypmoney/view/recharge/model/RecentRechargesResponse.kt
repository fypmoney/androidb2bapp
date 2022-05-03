package com.fypmoney.view.recharge.model

import com.google.gson.annotations.SerializedName

data class RecentRechargesResponse(

	@field:SerializedName("data")
	val data: List<RecentRechargeItem>
)

data class RecentRechargeItem(

	@field:SerializedName("orderType")
	val orderType: String? = null,

	@field:SerializedName("rewardOrderNo")
	val rewardOrderNo: Any? = null,

	@field:SerializedName("latitude")
	val latitude: Any? = null,

	@field:SerializedName("dueDate")
	val dueDate: Any? = null,

	@field:SerializedName("planName")
	val planName: Any? = null,

	@field:SerializedName("description")
	val description: Any? = null,

	@field:SerializedName("cardNo")
	val cardNo: String? = null,

	@field:SerializedName("isPurchased")
	val isPurchased: String? = null,

	@field:SerializedName("operatorName")
	val operatorName: String? = null,

	@field:SerializedName("uaaId")
	val uaaId: Int? = null,

	@field:SerializedName("reversalTxnNo")
	val reversalTxnNo: Any? = null,

	@field:SerializedName("acceptPayment")
	val acceptPayment: Any? = null,

	@field:SerializedName("cellNumber")
	val cellNumber: Any? = null,

	@field:SerializedName("mode")
	val mode: Any? = null,

	@field:SerializedName("billNetAmount")
	val billNetAmount: Any? = null,

	@field:SerializedName("billAmount")
	val billAmount: Any? = null,

	@field:SerializedName("requestOperatorId")
	val requestOperatorId: String? = null,

	@field:SerializedName("merchantResponseCode")
	val merchantResponseCode: Int? = null,

	@field:SerializedName("candidateForReversal")
	val candidateForReversal: String? = null,

	@field:SerializedName("isFullFilled")
	val isFullFilled: String? = null,

	@field:SerializedName("txnTime")
	val txnTime: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("planPrice")
	val planPrice: String? = null,

	@field:SerializedName("operatorId")
	val operatorId: Any? = null,

	@field:SerializedName("paymentStatus")
	val paymentStatus: String? = null,

	@field:SerializedName("merchantHttpCode")
	val merchantHttpCode: Int? = null,

	@field:SerializedName("longitude")
	val longitude: Any? = null,

	@field:SerializedName("planType")
	val planType: String? = null,

	@field:SerializedName("orderNo")
	val orderNo: String? = null,

	@field:SerializedName("cardType")
	val cardType: String? = null,

	@field:SerializedName("mobileNo")
	val mobileNo: String,

	@field:SerializedName("userName")
	val userName: Any? = null,

	@field:SerializedName("planCode")
	val planCode: Any? = null,

	@field:SerializedName("ackNo")
	val ackNo: String? = null,

	@field:SerializedName("merchantResponseMsg")
	val merchantResponseMsg: String? = null,

	@field:SerializedName("paymentIdentifier")
	val paymentIdentifier: String? = null,

	@field:SerializedName("billdate")
	val billdate: Any? = null,

	@field:SerializedName("acceptPartPay")
	val acceptPartPay: Any? = null,

	@field:SerializedName("circle")
	val circle: Any? = null,

	@field:SerializedName("currencyCode")
	val currencyCode: Any? = null,

	@field:SerializedName("txnDate")
	val txnDate: String? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("isReversed")
	val isReversed: String? = null
)
