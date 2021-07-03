package com.fypmoney.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ProfileImageUploadResponse(
    @SerializedName("data") var profileImageUploadResponseDetails: ProfileImageUploadResponseDetails?
) : Serializable

data class ProfileImageUploadResponseDetails(
    @SerializedName("accessUrl") var accessUrl: String?,
    @SerializedName("uaaId") var uaaId: String?
) : Serializable