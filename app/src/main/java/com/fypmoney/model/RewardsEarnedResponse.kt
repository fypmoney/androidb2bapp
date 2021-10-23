package com.fypmoney.model
import androidx.annotation.Keep

import com.google.gson.annotations.SerializedName


@Keep
data class RewardsEarnedResponse(
    val `data`: RewardEarnedData
)

@Keep
data class RewardEarnedData(
    val additionalInfo: String,
    val baseTxnId: String,
    val cashbackWonForProduct: String,
    val currencyCode: String,
    val customerId: Int,
    val eventDescription: String,
    val fullfillmentDescription: String,
    val id: Int,
    val isFullFilled: String,
    val loyaltyCampaignId: Int,
    val loyaltyEarned: String,
    val orderNumber: String,
    val partnerId: String,
    val pointConversionValue: String,
    val pointExpiryDate: String,
    val points: String? = null,
    val productCode: String,
    val productName: String,
    val productType: String,
    val ruleId: String,
    val status: String,
    val tierMasterId: String,
    val transactionType: String,
    val txnTime: String
)