package com.fypmoney.view.pocketmoneysettings.model

import androidx.annotation.Keep

@Keep
data class PocketMoneyAddReminderModel(
    var mobileNumber: Int,
    var name: String,
    var amount: Int,
    var frequency: String,
    var otpIdentifier: String
)