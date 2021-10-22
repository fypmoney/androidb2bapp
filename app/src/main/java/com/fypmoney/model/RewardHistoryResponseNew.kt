package com.fypmoney.model

data class RewardHistoryResponseNew(
    val date: String? = null,
    var history: ArrayList<HistoryItem?>? = null
)

data class HistoryItem(
	val loyaltyEarned: Any? = null,
	val cashbackWonForProduct: Int? = null,
	val orderNumber: String? = null,
	val productName: String? = null,
	val points: Int? = null,
	val transactionType: String? = null,
	val pointConversionValue: Any? = null,
	val pointExpiryDate: Any? = null,
	val productCode: String? = null,
	val loyaltyCampaignId: Any? = null,
	val baseTxnId: Any? = null,
	val customerId: Int? = null,
	val additionalInfo: Any? = null,
	val eventDescription: String? = null,
	val txnTime: String? = null,
	val isFullFilled: String? = null,
	val tierMasterId: Int? = null,
	val id: Int? = null,
	val partnerId: Int? = null,
	val ruleId: Any? = null,
	val currencyCode: String? = null,
	val productType: String? = null,
	val status: String? = null,
	val fullfillmentDescription: String? = null
)

