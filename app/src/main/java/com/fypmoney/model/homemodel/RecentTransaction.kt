package com.fypmoney.model.homemodel

data class RecentTransaction(
    val data: List<RecentTransactionUser>
)

data class RecentTransactionUser(
    val name: String,
    val profilePicResourceId: String,
    val userId: Int,
    val userMobile: String,
    val userName: String
)