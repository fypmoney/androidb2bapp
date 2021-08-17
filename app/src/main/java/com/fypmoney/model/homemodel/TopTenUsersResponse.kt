package com.fypmoney.model.homemodel

import androidx.annotation.Keep

@Keep
data class TopTenUsersResponse(
    val data: List<Users>
)
@Keep
data class Users(
    val name: String,
    val profilePicResourceId: String,
    val userId: Int,
    val userMobile: String,
    val userName: String
)