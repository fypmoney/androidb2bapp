package com.fypmoney.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AuthLoginResponse(
    @SerializedName("data") var authLoginResponseDetails: AuthLoginResponseDetails?
) : Serializable

data class AuthLoginResponseDetails(
    @SerializedName("access_token") var access_token: String?,
    @SerializedName("token_type") var token_type: String?,
    @SerializedName("expires_in") var expires_in: String?
) : Serializable

data class LoginInitResponse(
    @SerializedName("msg") var msg: String?
) : Serializable

data class LoginResponse(
    @SerializedName("data") var loginResponseDetails: LoginResponseDetails?
) : Serializable

data class LoginResponseDetails(
    @SerializedName("access_token") var access_token: String?,
    @SerializedName("token_type") var token_type: String?,
    @SerializedName("expires_in") var expires_in: String?,
    @SerializedName("userType") var userType: String?,
    @SerializedName("userEmail") var userEmail: String?,
    @SerializedName("userMobile") var userMobile: String?
    ) : Serializable

data class CustomerInfoResponse(
    @SerializedName("id") val id: Long?=0L,
    @SerializedName("login") val login: String?=null,
    @SerializedName("title") val title: String?=null,
    @SerializedName("firstName") val firstName: String?=null,
    @SerializedName("lastName") val lastName: String?=null,
    @SerializedName("email") var email: String?=null,
    @SerializedName("activated") val activated: Boolean?=false,
    @SerializedName("imageUrl") val imageUrl: String?=null,
    @SerializedName("resetDate") val resetDate: String?=null,
    @SerializedName("userType") val userType: String?=null,
    @SerializedName("status") val status: String?=null,
    @SerializedName("countryCode") val countryCode: String?=null,
    @SerializedName("mobile") var mobile: String?=null,
    @SerializedName("isMobileVerified") var isMobileVerified: String?=null,
    @SerializedName("isEmailVerified") var isEmailVerified: String?=null,
    @SerializedName("registrationMode") val registrationMode: String?=null,
    @SerializedName("lastLogin") val lastLogin: String?=null,
    @SerializedName("appId") val appId: String?=null,
    @SerializedName("token") val token: String?=null,
    @SerializedName("userInterests") val userInterests: ArrayList<UserInterestModel>?=null,
    @SerializedName("isProfileCompleted") val isProfileCompleted: String?=null,
    @SerializedName("isMandatoryProfileCompleted") val isMandatoryProfileCompleted: String?=null,
    @SerializedName("isReferralAllowed") val isReferralAllowed: String?=null,
    @SerializedName("idToken") val idToken: String?=null
) : Serializable

data class UserInterestModel(
    @SerializedName("id") var id: String?,
    @SerializedName("name") var name: String?,
    @SerializedName("code") var code: String?,
    @SerializedName("description") var description: String?,
    @SerializedName("status") var status: String?
) : Serializable

