package com.fypmoney.model

import com.fypmoney.database.entity.MemberEntity
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class MemberModel(var name: String? = null, var imageUrl: String? = null) {

}

data class AddFamilyMemberRequest(
    @SerializedName("mobileNo") val mobileNo: String,
    @SerializedName("name") val name: String?,
    @SerializedName("relation") val relation: String,
    @SerializedName("isGuarantor") val isGuarantor: String
) : BaseRequest()

data class AddFamilyMemberResponse(
    @SerializedName("mobileNo") val mobileNo: String,
    @SerializedName("name") val name: String,
    @SerializedName("relation") val relation: String,
    @SerializedName("relationDisplayName") val relationDisplayName: String,
    @SerializedName("familyName") val familyName: String,
    @SerializedName("status") val status: String,
) : Serializable

data class IsAppUserRequest(
    @SerializedName("mobileNo") val mobileNo: String
) : BaseRequest()

data class IsAppUserResponse(
    @SerializedName("data") val isAppUserResponseDetails: IsAppUserResponseDetails,
) : Serializable

data class IsAppUserResponseDetails(
    @SerializedName("userId") var userId: String?,
    @SerializedName("name") var name: String?,
    @SerializedName("isAppUser") var isAppUser: Boolean?
) : Serializable

data class GetMemberResponse(
    @SerializedName("data") val GetMemberResponseDetails: List<MemberEntity>
) : Serializable

data class GetMemberResponseDetails(
    @SerializedName("name") var name: String?,
    @SerializedName("mobileNo") var mobileNo: String?,
    @SerializedName("relation") var relation: String?,
    @SerializedName("userId") var userId: Double?,
    @SerializedName("relationDisplayName") var relationDisplayName: String?,
    @SerializedName("familyName") var familyName: String?,
    @SerializedName("status") var status: String?
) : Serializable

data class LeaveFamilyResponse(
    @SerializedName("msg") var msg: String?
) : Serializable

data class RemoveFamilyResponse(
    @SerializedName("msg") var msg: String?
) : Serializable

data class FamilyNotificationRequest(
    @SerializedName("id") val id: String?=null,
    @SerializedName("sourceUserId") val sourceUserId: Int?=null,
    @SerializedName("destinationUserId") val destinationUserId: Int?=null,
    @SerializedName("requestCategoryCode") val requestCategoryCode: String?=null,
) : BaseRequest()

data class FamilyNotificationResponse(
    @SerializedName("data") val familyNotificationResponseDetails: List<FamilyNotificationResponseDetails>
) : Serializable

data class UpdateFamilyApprovalResponse(
    @SerializedName("data") val familyNotificationResponseDetails: FamilyNotificationResponseDetails
) : Serializable


data class FamilyNotificationResponseDetails(
    @SerializedName("id") var id: String?=null,
    @SerializedName("sourceUserId") var sourceUserId: Int?=null,
    @SerializedName("destinationUserId") var destinationUserId: Int?=null,
    @SerializedName("actionAllowed") var actionAllowed: String?=null,
    @SerializedName("requestStatus") var requestStatus: String?=null,
    @SerializedName("requestHandler") var requestHandler: String?=null,
    @SerializedName("expiryDate") var expiryDate: String?=null,
    @SerializedName("isParentRequest") var isParentRequest: String?=null,
    @SerializedName("parentId") var parentId: String?=null,
    @SerializedName("entityId") var entityId: String?=null,
    @SerializedName("isApprovalProcessed") var isApprovalProcessed: String?=null,
    @SerializedName("objectJson") var objectJson: String?=null,
    @SerializedName("description") var description: String?=null,
    @SerializedName("entityType") var entityType: String?=null,
    @SerializedName("status") var status: String?=null,
    @SerializedName("actionSelected") var actionSelected: String?=null,

    ) : Serializable

data class UpdateFamilyApprovalRequest(
    @SerializedName("id") val id: String?=null,
    @SerializedName("actionSelected") val actionSelected: String?=null,
    @SerializedName("sourceUserId") var sourceUserId: Int?,
    @SerializedName("destinationUserId") var destinationUserId: Int?,
    @SerializedName("actionAllowed") var actionAllowed: String?,
    @SerializedName("requestStatus") var requestStatus: String?,
    @SerializedName("requestHandler") var requestHandler: String?,
    @SerializedName("expiryDate") var expiryDate: String?,
    @SerializedName("isParentRequest") var isParentRequest: String?,
    @SerializedName("parentId") var parentId: String?,
    @SerializedName("entityId") var entityId: String?,
    @SerializedName("isApprovalProcessed") var isApprovalProcessed: String?,
    @SerializedName("objectJson") var objectJson: String?,
    @SerializedName("description") var description: String?,
    @SerializedName("entityType") var entityType: String?,
    @SerializedName("status") var status: String?) : BaseRequest()

