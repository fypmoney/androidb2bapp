package com.fypmoney.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class GetRewardsResponseDetails(
    @SerializedName("uaaId") var uaaId: String?,
    @SerializedName("spinAllowedToday") var spinAllowedToday: String?,
    @SerializedName("totalReward") var totalReward: String?,
    @SerializedName("rewards") var rewards: Rewards?,
    @SerializedName("pointsToRedeem") var pointsToRedeem: String?

) : Serializable
@Keep
data class Rewards(
    @SerializedName("uaaId") var uaaId: String?,
    @SerializedName("sectionId") var sectionId: String?,
    @SerializedName("sectionName") var sectionName: String?,
    @SerializedName("sectionValue") var sectionValue: String?,
    @SerializedName("playedOn") var playedOn: String?,
    @SerializedName("rewardType") var rewardType: String?
) : Serializable

@Keep
data class SpinWheelResponseDetails(
    @SerializedName("sectionId") var sectionId: Int?,
    @SerializedName("sectionName") var sectionName: String?,
    @SerializedName("colorCode") var colorCode: String?
) : Serializable

@Keep
data class SpinWheelRotateResponseDetails(
    @SerializedName("message") var message: String?,
    @SerializedName("cashbackWon") var cashbackWon: String?,
    @SerializedName("sectionValue") var sectionValue: String?,
    @SerializedName("sectionCode") var sectionCode: String?,
    @SerializedName("couponCode") var couponCode: String?,
    @SerializedName("sectionId") var sectionId: Int?,
    @SerializedName("noOfJackpotTicket") var noOfJackpotTicket: Int?,
    @SerializedName("myntsWon") var myntsWon: Int?

) : Serializable



@Keep
data class GetRewardsHistoryResponseDetails(
    @SerializedName("id") var id: Int?,
    @SerializedName("uaaId") var uaaId: String?,
    @SerializedName("sectionId") var sectionId: String?,
    @SerializedName("sectionName") var sectionName: String?,
    @SerializedName("sectionValue") var sectionValue: String?,
    @SerializedName("playedOn") var playedOn: String?,
    @SerializedName("rewardType") var rewardType: String?,
    @SerializedName("rewardTxnType") var rewardTxnType: String?
) : Serializable

