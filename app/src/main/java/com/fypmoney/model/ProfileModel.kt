package com.fypmoney.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable
@Keep
data class ProfileImageUploadResponse(
    @SerializedName("data") var profileImageUploadResponseDetails: ProfileImageUploadResponseDetails?
) : Serializable
@Keep
data class ProfileImageUploadResponseDetails(
    @SerializedName("accessUrl") var accessUrl: String?,
    @SerializedName("uaaId") var uaaId: String?
) : Serializable