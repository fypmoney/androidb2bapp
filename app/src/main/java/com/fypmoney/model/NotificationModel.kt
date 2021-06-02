package com.fypmoney.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class NotificationModel {
    data class NotificationRequest(
        @SerializedName("id") val id: String? = null,
        @SerializedName("sourceUserId") val sourceUserId: Int? = null,
        @SerializedName("destinationUserId") val destinationUserId: Int? = null,
        @SerializedName("requestCategoryCode") val requestCategoryCode: String? = null,
    ) : BaseRequest()

    data class NotificationResponse(
        @SerializedName("data") val notificationResponseDetails: List<NotificationResponseDetails>
    ) : Serializable

    data class NotificationResponseById(
        @SerializedName("data") val notificationResponseDetails: NotificationResponseDetails
    ) : Serializable

    data class NotificationResponseDetails(
        @SerializedName("id") var id: String?=null,
        @SerializedName("sourceUserId") var sourceUserId: Int?=null,
        @SerializedName("destinationUserId") var destinationUserId: Int?=null,
        @SerializedName("requestStatus") var requestStatus: String?=null,
        @SerializedName("isParentRequest") var isParentRequest: String?=null,
        @SerializedName("entityId") var entityId: String?=null,
        @SerializedName("isApprovalProcessed") var isApprovalProcessed: String?=null,
        @SerializedName("description") var description: String?=null,
        @SerializedName("entityType") var entityType: String?=null,
        @SerializedName("requestCategoryCode") var requestCategoryCode: String?=null,
        @SerializedName("actionAllowed") var actionAllowed: String?=null,
        @SerializedName("actionSelected") var actionSelected: String?=null,
        @SerializedName("appDisplayAction") var appDisplayAction: String?=null

    ) : Serializable

    data class UserTimelineResponse(
        @SerializedName("data") val notificationResponseDetails: List<UserTimelineResponseDetails>
    ) : Serializable

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
        @SerializedName("requestCategoryCode") var requestCategoryCode: String?=null) : Serializable


}