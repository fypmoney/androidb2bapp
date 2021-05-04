package com.dreamfolks.baseapp.model

import com.dreamfolks.baseapp.database.entity.MemberEntity
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class MemberModel(var name: String? = null, var imageUrl: String? = null) {

}

data class AddFamilyMemberRequest(
    @SerializedName("mobileNo") val mobileNo: String,
    @SerializedName("name") val name: String?,
    @SerializedName("relation") val relation: String
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