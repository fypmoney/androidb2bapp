package com.fypmoney.model

import android.os.Build
import androidx.annotation.Keep
import com.fypmoney.BuildConfig
import com.fypmoney.util.AppConstants
import com.google.gson.annotations.SerializedName
import java.io.Serializable

open class BaseRequest
@Keep
data class LoginInitRequest(
    @SerializedName("identifierType") val identifierType: String,
    @SerializedName("identifier") val identifier: String
) : BaseRequest()

@Keep
data class UpdateProfileRequest(
    @SerializedName("userId") val userId: Long? = 0,
    @SerializedName("firstName") val firstName: String? = null,
    @SerializedName("lastName") val lastName: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("mobile") var mobile: String? = null,
    @SerializedName("imageUrl") val imageUrl: String? = null,
    @SerializedName("dob") val dob: String? = null,
    @SerializedName("anniversary") val anniversary: String? = null,
    @SerializedName("cityName") val cityName: String? = null,
    @SerializedName("schoolName") val schoolName: String? = null,
    @SerializedName("cityId") val cityId: Int? = null,
    @SerializedName("subscribedToPromotions") val subscribedToPromotions: String? = null,
    @SerializedName("interest") val interest: ArrayList<InterestEntity>?=null,
    ) : BaseRequest()


@Keep
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
@Keep
data class UserDeviceInfo(
    @SerializedName("make") val make: String? = null,
    @SerializedName("model") val model: String? = null,
    @SerializedName("modelVersion") val modelVersion: String? = null,
    @SerializedName("timezone") val timezone: String? = null,
    @SerializedName("locale") val locale: String? = null,
    @SerializedName("appVersion") val appVersion: Int = BuildConfig.VERSION_CODE,
    @SerializedName("platform") val platform: String = AppConstants.CLIENT_TYPE_VALUE,
    @SerializedName("platformVersion") val platformVersion: String = Build.VERSION.SDK_INT.toString(),
    @SerializedName("dtoken") val dtoken: String? = null,
    @SerializedName("lat") val latitude: String? = null,
    @SerializedName("long") val longitude: String? = null,
    @SerializedName("userId") val userId: Long? = null,
    @SerializedName("rfu1") val rfu1: String? = null,
    @SerializedName("isHomeViewed") val isHomeViewed: String? = "YES"


) : Serializable
@Keep
data class ReferralCodeResponse(
    @SerializedName("msg") var msg: String?
) : Serializable

@Keep
data class SetPocketMoneyReminder(
    @SerializedName("identifierType") val identifierType: String? = null,
    @SerializedName("mobile") val mobile: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("amount") val amount: Int? = null,
    @SerializedName("frequency") val frequency: String? = null,
    @SerializedName("relation") val relation: String? = null
) : BaseRequest()

@Keep
data class SetPocketMoneyOtpReminder(
    @SerializedName("identifierType") val identifierType: String? = null,
    @SerializedName("otpIdentifier") val otpIdentifier: String? = null,
    @SerializedName("otp") val otp: String? = null,
) : BaseRequest()

@Keep
data class VerifyOtp(
    @SerializedName("otpIdentifier") val otpIdentifier: String? = null,
    @SerializedName("otp") val otp: String? = null,
) : BaseRequest()

@Keep
data class DeletePocketMoneyReminder(
    @SerializedName("mobile") val mobile: String? = null
) : BaseRequest()

