package com.fypmoney.model

import androidx.annotation.Keep
import com.fypmoney.database.entity.MemberEntity
import com.google.gson.annotations.SerializedName
import java.io.Serializable


@Keep
data class AddFamilyMemberRequest(
    @SerializedName("mobileNo") val mobileNo: String,
    @SerializedName("name") val name: String?,
    @SerializedName("relation") val relation: String,
    @SerializedName("isGuarantor") val isGuarantor: String?=null
) : BaseRequest()
@Keep
data class AddFamilyMemberResponse(
    @SerializedName("mobileNo") val mobileNo: String,
    @SerializedName("name") val name: String,
    @SerializedName("relation") val relation: String,
    @SerializedName("relationDisplayName") val relationDisplayName: String,
    @SerializedName("familyName") val familyName: String,
    @SerializedName("status") val status: String,
) : Serializable
@Keep
data class IsAppUserRequest(
    @SerializedName("mobileNo") val mobileNo: String
) : BaseRequest()
@Keep
data class IsAppUserResponse(
    @SerializedName("data") val isAppUserResponseDetails: IsAppUserResponseDetails,
) : Serializable
@Keep
data class IsAppUserResponseDetails(
    @SerializedName("userId") var userId: String?,
    @SerializedName("name") var name: String?,
    @SerializedName("isAppUser") var isAppUser: Boolean?,
    @SerializedName("profilePicResourceId") var profilePicResourceId: String?
) : Serializable
@Keep
data class GetMemberResponse(
    @SerializedName("data") val GetMemberResponseDetails: List<MemberEntity>
) : Serializable
@Keep
data class GetMemberResponseDetails(
    @SerializedName("name") var name: String?,
    @SerializedName("mobileNo") var mobileNo: String?,
    @SerializedName("relation") var relation: String?,
    @SerializedName("userId") var userId: Double?,
    @SerializedName("relationDisplayName") var relationDisplayName: String?,
    @SerializedName("familyName") var familyName: String?,
    @SerializedName("status") var status: String?,
    @SerializedName("profilePicResourceId") var profilePicResourceId: String?,
    @SerializedName("isGuarantor") var isGuarantor: String?,
    @SerializedName("iconNo") var iconNo: String?,


    ) : Serializable
@Keep
data class LeaveFamilyResponse(
    @SerializedName("msg") var msg: String?
) : Serializable
@Keep
data class RemoveFamilyResponse(
    @SerializedName("msg") var msg: String?
) : Serializable



@Keep
data class UpdateFamilyApprovalResponse(
    @SerializedName("msg") val msg: String?=null,
    @SerializedName("data") val notificationResponseDetails: NotificationModel.NotificationResponseDetails
) : Serializable





@Keep
data class UpdateFamilyNameResponse(
    @SerializedName("data") val updateFamilyNameDetails:UpdateFamilyNameDetails
) : Serializable
@Keep
data class UpdateFamilyNameDetails(
    @SerializedName("name") var name: String?
) : Serializable

