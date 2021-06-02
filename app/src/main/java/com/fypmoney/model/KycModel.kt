package com.fypmoney.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class KycModel {
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
        @SerializedName("message") val message: String,
        @SerializedName("mobileNo") val mobileNo: String,
        @SerializedName("showMobileOtpVerificationScreen") val showMobileOtpVerificationScreen: String,
        @SerializedName("showAdharInitScreen") val showAdharInitScreen: String,
        @SerializedName("showAdharOtpVerificationScreen") val showAdharOtpVerificationScreen: String,
        @SerializedName("action") val action: String,
        @SerializedName("token") val token: String
    ) : Serializable

}