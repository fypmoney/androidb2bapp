package com.fypmoney.model.homemodel

data class TopTenUsersResponse(
    val data: List<Users>
)

data class Users(
    val name: String,
    val profilePicResourceId: String,
    val userId: Int,
    val userMobile: String,
    val userName: String
)