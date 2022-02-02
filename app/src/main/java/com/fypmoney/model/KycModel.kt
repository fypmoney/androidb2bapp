package com.fypmoney.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class KycActivateAccountResponse(
    @SerializedName("data") val kycActivateAccountResponseDetails: KycActivateAccountResponseDetails
) : Serializable

@Keep
data class KycActivateAccountResponseDetails(
    @SerializedName("message") val message: String,
    @SerializedName("mobileNo") val mobileNo: String,
    @SerializedName("showMobileOtpVerificationScreen") val showMobileOtpVerificationScreen: String,
    @SerializedName("showAdharInitScreen") val showAdharInitScreen: String,
    @SerializedName("showAdharOtpVerificationScreen") val showAdharOtpVerificationScreen: String,
    @SerializedName("action") val action: String,
    @SerializedName("token") val token: String,
    @SerializedName("postKycScreenCode") val postKycScreenCode: String,

    ) : Serializable

@Keep
data class KycMobileVerifyRequest(
    @SerializedName("otp") val otp: String,
    @SerializedName("token") val token: String,
    @SerializedName("action") val action: String
) : BaseRequest()
@Keep
data class KycMobileVerifyResponse(
    @SerializedName("data") val kycMobileVerifyResponseDetails: KycMobileVerifyResponseDetails
) : Serializable
@Keep
data class KycMobileVerifyResponseDetails(
    @SerializedName("expiryTime") val expiryTime: Int,
    @SerializedName("showMobileOtpVerificationScreen") val showMobileOtpVerificationScreen: String,
    @SerializedName("showAdharInitScreen") val showAdharInitScreen: String,
    @SerializedName("showAdharOtpVerificationScreen") val showAdharOtpVerificationScreen: String,
    @SerializedName("message") val message: String,
    @SerializedName("postKycScreenCode") val postKycScreenCode: String,

    ) : Serializable

@Keep
data class KycInitRequest(
    @SerializedName("kycMode") val kycMode : String,
    @SerializedName("kycType") val kycType: String,
    @SerializedName("documentIdentifier") val documentIdentifier: String,
    @SerializedName("documentType") val documentType: String,
    @SerializedName("action") val action: String,
) : BaseRequest()
@Keep
data class KycInitResponse(
    @SerializedName("data") val kycInitResponseDetails: KycInitResponseDetails
) : Serializable
@Keep
data class KycInitResponseDetails(
    @SerializedName("showMobileOtpVerificationScreen") val showMobileOtpVerificationScreen: String,
    @SerializedName("showAdharInitScreen") val showAdharInitScreen: String,
    @SerializedName("showAdharOtpVerificationScreen") val showAdharOtpVerificationScreen: String,
    @SerializedName("message") val message: String,
    @SerializedName("documentIdentifier") val documentIdentifier: String,
    @SerializedName("documentType") val documentType: String,
    @SerializedName("kycType") val kycType: String,
    @SerializedName("token") val token: String
):Serializable

@Keep
data class KycVerificationRequest(
    @SerializedName("otp") val otp: String,
    @SerializedName("token") val token: String,
    @SerializedName("action") val action: String
) : BaseRequest()

@Keep
data class KycVerificationResponse(
    @SerializedName("data") val kycVerificationResponseDetails: KycVerificationResponseDetails
) : Serializable
@Keep
data class KycVerificationResponseDetails(
   @SerializedName("message") val message: String
):Serializable




