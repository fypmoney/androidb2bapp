package com.fypmoney.model.yourTaskModal

data class Data(
    val actionAllowed: String,
    val additionalAttributes: AdditionalAttributes,
    val appDisplayAction: String,
    val description: String,
    val destinationUserId: Int,
    val entityId: Int,
    val entityType: String,
    val expiryDate: String,
    val id: String,
    val isApprovalProcessed: String,
    val isParentRequest: String,
    val listDescription: String,
    val objectJson: String,
    val parentId: String,
    val processStatus: Int,
    val requestCategoryCode: String,
    val requestStatus: String,
    val sourceUserId: Int,
    val status: String
)