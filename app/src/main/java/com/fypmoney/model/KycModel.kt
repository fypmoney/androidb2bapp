package com.fypmoney.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class KycActivateAccountResponse(
    @SerializedName("data") val kycActivateAccountResponseDetails: KycActivateAccountResponseDetails
) : Serializable


data class KycActivateAccountResponseDetails(
    @SerializedName("message") val message: String,
    @SerializedName("mobileNo") val mobileNo: String,
    @SerializedName("showMobileOtpVerificationScreen") val showMobileOtpVerificationScreen: String,
    @SerializedName("showAdharInitScreen") val showAdharInitScreen: String,
    @SerializedName("showAdharOtpVerificationScreen") val showAdharOtpVerificationScreen: String,
    @SerializedName("action") val action: String,
    @SerializedName("token") val token: String
) : Serializable


data class KycMobileVerifyRequest(
    @SerializedName("otp") val otp: String,
    @SerializedName("token") val token: String,
    @SerializedName("action") val action: String
) : BaseRequest()

data class KycMobileVerifyResponse(
    @SerializedName("data") val kycMobileVerifyResponseDetails: KycMobileVerifyResponseDetails
) : Serializable

data class KycMobileVerifyResponseDetails(
    @SerializedName("expiryTime") val expiryTime: Int,
    @SerializedName("showMobileOtpVerificationScreen") val showMobileOtpVerificationScreen: String,
    @SerializedName("showAdharInitScreen") val showAdharInitScreen: String,
    @SerializedName("showAdharOtpVerificationScreen") val showAdharOtpVerificationScreen: String,
    @SerializedName("message") val message: String
) : Serializable


data class KycInitRequest(
    @SerializedName("kycMode") val kycMode : String,
    @SerializedName("kycType") val kycType: String,
    @SerializedName("documentIdentifier") val documentIdentifier: String,
    @SerializedName("documentType") val documentType: String,
    @SerializedName("action") val action: String,
) : BaseRequest()

data class KycInitResponse(
    @SerializedName("data") val kycInitResponseDetails: KycInitResponseDetails
) : Serializable

data class KycInitResponseDetails(
    @SerializedName("showMobileOtpVerificationScreen") val showMobileOtpVerificationScreen: String,
    @SerializedName("showAdharInitScreen") val showAdharInitScreen: String,
    @SerializedName("showAdharOtpVerificationScreen") val showAdharOtpVerificationScreen: String,
    @SerializedName("message") val message: String,
    @SerializedName("documentIdentifier") val documentIdentifier: String,
    @SerializedName("documentType") val documentType: String,
    @SerializedName("token") val token: String
):Serializable


data class KycVerificationRequest(
    @SerializedName("otp") val otp: String,
    @SerializedName("token") val token: String,
    @SerializedName("action") val action: String
) : BaseRequest()


data class KycVerificationResponse(
    @SerializedName("data") val kycVerificationResponseDetails: KycVerificationResponseDetails
) : Serializable

data class KycVerificationResponseDetails(
   @SerializedName("message") val message: String
):Serializable




