package com.fypmoney.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class NotificationModel {
    @Keep
    data class NotificationRequest(
        @SerializedName("id") val id: String? = null,
        @SerializedName("sourceUserId") val sourceUserId: Int? = null,
        @SerializedName("destinationUserId") val destinationUserId: Int? = null,
        @SerializedName("requestCategoryCode") val requestCategoryCode: String? = null
    ) : BaseRequest()
    @Keep
    data class NotificationResponse(
        @SerializedName("data") val notificationResponseDetails: List<NotificationResponseDetails>
    ) : Serializable
    @Keep
    data class NotificationResponseById(
        @SerializedName("data") val notificationResponseDetails: NotificationResponseDetails
    ) : Serializable
    @Keep
    data class NotificationResponseDetails(
        @SerializedName("id") var id: String?=null,
        @SerializedName("sourceUserId") var sourceUserId: Int?=null,
        @SerializedName("destinationUserId") var destinationUserId: Int?=null,
        @SerializedName("destinationUserMobile") var destinationUserMobile: String?=null,
        @SerializedName("destinationUserProfilePic") var destinationUserProfilePic: String?=null,
        @SerializedName("requestStatus") var requestStatus: String?=null,
        @SerializedName("isParentRequest") var isParentRequest: String?=null,
        @SerializedName("entityId") var entityId: String?=null,
        @SerializedName("isApprovalProcessed") var isApprovalProcessed: String?=null,
        @SerializedName("description") var description: String?=null,
        @SerializedName("entityType") var entityType: String?=null,
        @SerializedName("requestCategoryCode") var requestCategoryCode: String?=null,
        @SerializedName("actionAllowed") var actionAllowed: String?=null,
        @SerializedName("actionSelected") var actionSelected: String?=null,
        @SerializedName("appDisplayAction") var appDisplayAction: String?=null,
        @SerializedName("sourceUserName") var sourceUserName: String?=null,
        @SerializedName("icon") var icon: String?=null,
        @SerializedName("destinationUserName") var destinationUserName: String?=null,
        @SerializedName("objectJson") var objectJson: String?=null,
        @SerializedName("name") var name: String?=null

        ) : Serializable
    @Keep
    data class UserTimelineResponse(
        @SerializedName("data") val notificationResponseDetails: List<UserTimelineResponseDetails>
    ) : Serializable
    @Keep
    data class  UserTimelineResponseDetails(
        @SerializedName("id") var id: String?=null,
        @SerializedName("sourceUserId") var sourceUserId: Int?=null,
        @SerializedName("destinationUserId") var destinationUserId: Int?=null,
        @SerializedName("requestStatus") var requestStatus: String?=null,
        @SerializedName("isParentRequest") var isParentRequest: String?=null,
        @SerializedName("entityId") var entityId: String?=null,
        @SerializedName("isApprovalProcessed") var isApprovalProcessed: String?=null,
        @SerializedName("description") var description: String?=null,
        @SerializedName("entityType") var entityType: String?=null,
        @SerializedName("icon") var icon: String?=null,
        @SerializedName("name") var name: String?=null,
        @SerializedName("requestCategoryCode") var requestCategoryCode: String?=null) : Serializable
}