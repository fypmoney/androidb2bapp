package com.dreamfolks.baseapp.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

open class BaseRequest

data class LoginInitRequest(
    @SerializedName("identifierType") val identifierType: String,
    @SerializedName("identifier") val identifier: String
) : BaseRequest()


data class UpdateProfileRequest(
    @SerializedName("userId") val userId: Long? = 0,
    @SerializedName("firstName") val firstName: String? = null,
    @SerializedName("lastName") val lastName: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("mobile") var mobile: String? = null,
    @SerializedName("imageUrl") val imageUrl: String? = null,
    @SerializedName("dob") val dob: String? = null,
    @SerializedName("anniversary") val anniversary: String? = null,
    @SerializedName("cityId") val cityId: Int? = null,
    @SerializedName("subscribedToPromotions") val subscribedToPromotions: String? = null,
    @SerializedName("interest") val interest: ArrayList<InterestEntity>,

    ) : BaseRequest()


data class AppLoginApiRequest(
    @SerializedName("identifierType") val identifierType: String? = null,
    @SerializedName("identifier") val identifier: String? = null,
    @SerializedName("countryCode") val countryCode: String? = null,
    @SerializedName("regMode") val regMode: String? = null,
    @SerializedName("otp") val otp: String? = null,
    @SerializedName("appClientId") val appClientId: String? = null,
    @SerializedName("idToken") val idToken: String? = null,
    @SerializedName("profile") val profile: UpdateProfileRequest? = null,
    @SerializedName("userDeviceInfo") val userDeviceInfo: UserDeviceInfo? = null
) : BaseRequest()

data class UserDeviceInfo(
    @SerializedName("make") val make: String,
    @SerializedName("model") val model: String,
    @SerializedName("modelVersion") val modelVersion: String,
    @SerializedName("timezone") val timezone: String,
    @SerializedName("locale") val locale: String,
    @SerializedName("appVersion") val appVersion: String,
    @SerializedName("platform") val platform: String,
    @SerializedName("platformVersion") val platformVersion: String,
    @SerializedName("dtoken") val dtoken: String
) : Serializable

