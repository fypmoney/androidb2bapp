package com.fypmoney.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class AuthLoginResponse(
    @SerializedName("data") var authLoginResponseDetails: AuthLoginResponseDetails?
) : Serializable
@Keep
data class AuthLoginResponseDetails(
    @SerializedName("access_token") var access_token: String?,
    @SerializedName("token_type") var token_type: String?,
    @SerializedName("expires_in") var expires_in: String?
) : Serializable
@Keep
data class LoginInitResponse(
    @SerializedName("msg") var msg: String?
) : Serializable
@Keep
data class LoginResponse(
    @SerializedName("data") var loginResponseDetails: LoginResponseDetails?
) : Serializable
@Keep
data class LoginResponseDetails(
    @SerializedName("access_token") var access_token: String?,
    @SerializedName("token_type") var token_type: String?,
    @SerializedName("expires_in") var expires_in: String?,
    @SerializedName("userType") var userType: String?,
    @SerializedName("userEmail") var userEmail: String?,
    @SerializedName("userMobile") var userMobile: String?
) : Serializable

@Keep
data class CustomerInfoResponse(
    @SerializedName("data") var customerInfoResponseDetails: CustomerInfoResponseDetails?
) : Serializable
@Keep
data class CustomerInfoResponseDetails(
    @SerializedName("id") val id: Long? = 0L,
    @SerializedName("login") val login: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("firstName") val firstName: String? = null,
    @SerializedName("lastName") val lastName: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("activated") val activated: Boolean? = false,
    @SerializedName("imageUrl") val imageUrl: String? = null,
    @SerializedName("resetDate") val resetDate: String? = null,
    @SerializedName("userType") val userType: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("countryCode") val countryCode: String? = null,
    @SerializedName("dob") val dob: String? = null,
    @SerializedName("mobile") var mobile: String? = null,
    @SerializedName("userProfile") var userProfile: UserProfile? = null,
    @SerializedName("isMobileVerified") var isMobileVerified: String? = null,
    @SerializedName("isEmailVerified") var isEmailVerified: String? = null,
    @SerializedName("registrationMode") val registrationMode: String? = null,
    @SerializedName("lastLogin") val lastLogin: String? = null,
    @SerializedName("appId") val appId: String? = null,
    @SerializedName("token") val token: String? = null,
    @SerializedName("userInterests") val userInterests: ArrayList<UserInterestModel>? = null,
    @SerializedName("isProfileCompleted") var isProfileCompleted: String? = null,
    @SerializedName("isMandatoryProfileCompleted") val isMandatoryProfileCompleted: String? = null,
    @SerializedName("isReferralAllowed") var isReferralAllowed: String? = null,
    @SerializedName("idToken") val idToken: String? = null,
    @SerializedName("referralCode") val referralCode: String? = null,
    @SerializedName("referralMsg") val referralMsg: String? = null,
    @SerializedName("referredCount") val referredCount: Int? = null,
    @SerializedName("bankProfile") val bankProfile: BankProfile? = null,
    @SerializedName("profilePicResourceId") val profilePicResourceId: String? = null,
    @SerializedName("cardProductCode") val cardProductCode: String? = null,
    @SerializedName("postKycScreenCode") val postKycScreenCode: String? = null,


    @SerializedName("parent") val parent: Any? = null,

    @SerializedName("userLevel") val userLevel: Any? = null,


    @SerializedName("isHomeViewed") val isHomeViewed: Any? = null,

    @SerializedName("rfu2") val rfu2: Any? = null,

    @SerializedName("rfu1") val rfu1: Any? = null,

    @SerializedName("langKey") val langKey: String? = null,


    @SerializedName("idAccessToken") val idAccessToken: Any? = null
) : Serializable


@Keep
data class UserProfile(
    @SerializedName("cityName") val cityName: String? = null,
    @SerializedName("dob") val dob: String? = null,
    @SerializedName("schoolName") val schoolName: String? = null,


    @SerializedName("gender") val gender: String? = null,

    @SerializedName("subscribedToPromotions") val subscribedToPromotions: Any? = null,
    @SerializedName("id") val id: Int? = null,
    @SerializedName("cityId") val cityId: Any? = null,

    @SerializedName("anniversary") val anniversary: Any? = null
) : Serializable

@Keep
data class BankProfile(
    @SerializedName("isAccountActive") var isAccountActive: String?,
    @SerializedName("isVirtualCardIssued") var isVirtualCardIssued: String?,
    @SerializedName("isPhysicardIssued") var isPhysicardIssued: String?
) : Serializable
@Keep
data class UserInterestModel(
    @SerializedName("id") var id: String?,
    @SerializedName("name") var name: String?,
    @SerializedName("code") var code: String?,
    @SerializedName("description") var description: String?,
    @SerializedName("status") var status: String?

) : Serializable
@Keep
data class LogOutResponse(
    @SerializedName("msg") var msg: String?
) : Serializable

