package com.fypmoney.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GetRewardsResponse(
    @SerializedName("data") val getRewardsResponseDetails: GetRewardsResponseDetails,
) : Serializable

data class GetRewardsResponseDetails(
    @SerializedName("uaaId") var uaaId: String?,
    @SerializedName("spinAllowedToday") var spinAllowedToday: String?,
    @SerializedName("totalReward") var totalReward: String?,
    @SerializedName("rewards") var rewards: Rewards?,
    @SerializedName("pointsToRedeem") var pointsToRedeem: String?

) : Serializable

data class Rewards(
    @SerializedName("uaaId") var uaaId: String?,
    @SerializedName("sectionId") var sectionId: String?,
    @SerializedName("sectionName") var sectionName: String?,
    @SerializedName("sectionValue") var sectionValue: String?,
    @SerializedName("playedOn") var playedOn: String?,
    @SerializedName("rewardType") var rewardType: String?
) : Serializable

data class SpinWheelResponse(
    @SerializedName("data") val spinWheelResponseDetails: SpinWheelResponseDetails
) : Serializable

data class SpinWheelResponseDetails(
    @SerializedName("sectionId") var sectionId: Int?,
    @SerializedName("sectionName") var sectionName: String?,
    @SerializedName("colorCode") var colorCode: String?
) : Serializable

data class GetRewardsHistoryResponse(
    @SerializedName("data") val getRewardsResponseDetails: List<GetRewardsHistoryResponseDetails>
) : Serializable

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

