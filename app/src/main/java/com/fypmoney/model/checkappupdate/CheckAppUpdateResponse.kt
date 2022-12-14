package com.fypmoney.model.checkappupdate

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class CheckAppUpdateResponse(
    @SerializedName("data") var appUpdateData: AppUpdateData?

)
@Keep
data class AppUpdateData(
    @SerializedName("id") var id: String,
    @SerializedName("currentVersion") var currentVersion: Int,
    @SerializedName("targetVersion") var targetVersion: Int,
    @SerializedName("updateFlag") var updateFlag: String
)


