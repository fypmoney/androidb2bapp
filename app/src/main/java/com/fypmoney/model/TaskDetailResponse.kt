package com.fypmoney.model

import androidx.annotation.Keep

@Keep
data class TaskDetailResponse(
    val processStatus: Int? = null,
    val icon: String? = null,
    val description: String? = null,
    val destinationUserMobile: String? = null,
    val actionSelected: Any? = null,
    val expiryDate: Any? = null,
    val destinationUserName: String? = null,
    val isParentRequest: String? = null,
    val requestHandler: Any? = null,
    val objectJson: String? = null,
    val id: String? = null,
    val appDisplayAction: String? = null,
    val destinationUserId: Int? = null,
    val emojis: Any? = null,
    val comments: Any? = null,
    val appScreenId: Any? = null,
    val sourceUserName: String? = null,
    val entityType: String? = null,
    val isApprovalProcessed: String? = null,
    val requestCategoryCode: String? = null,
    val entityId: Int? = null,
    val destinationStatus: String? = null,
    val parentId: String? = null,
    val sourceUserMobile: String? = null,
    val name: String? = null,
    val sourceUserId: Int? = null,
    val listDescription: String? = null,
    val requestStatus: String? = null,
    val actionAllowed: String? = null,
    val status: String? = null,
    val additionalAttributes: AdditionalAttributes? = null


)
@Keep
data class AdditionalAttributes(
    val amount: Int? = null,
    val endDate: String? = null,
    val worthPrice: String? = null,
    val listDetails: String? = null,
    val description: String? = null,
    val numberOfDays: Int? = null,
    val title: String? = null,
    val isPayable: String? = null,
    val startDate: String? = null
)

