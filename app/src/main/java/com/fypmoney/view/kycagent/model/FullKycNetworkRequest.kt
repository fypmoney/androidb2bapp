package com.fypmoney.view.kycagent.model

import androidx.annotation.Keep

@Keep
data class FullKycNetworkRequest(
    val customerMobile:String,
    val customerAadhaarNumber:String,
    val currentAddressCheck:String,
    val selectedFinger:String,
    val capturedInfo:String,
    val deviceSerialNumber:String,
    val deviceType:String,
    val deviceVersionNo:String,
    val deviceCertificateExpriy:String,
    val collectableAmount:String
)

@Keep
data class FullKycResponse(
    val msg:String,
    val data:Data,
)

@Keep
data class Data(
    val message:String
)