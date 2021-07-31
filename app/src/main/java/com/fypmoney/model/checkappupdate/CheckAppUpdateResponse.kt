package com.fypmoney.model.checkappupdate

import com.google.gson.annotations.SerializedName

data class CheckAppUpdateResponse(
    @SerializedName("data") var appUpdateData: AppUpdateData?

)

data class AppUpdateData(
    @SerializedName("id") var id: String,
    @SerializedName("currentVersion") var currentVersion: Int,
    @SerializedName("targetVersion") var targetVersion: Int,
    @SerializedName("updateFlag") var updateFlag: String
)


