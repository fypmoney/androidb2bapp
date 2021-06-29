package com.fypmoney.view

import com.fypmoney.model.UpDateCardSettingsRequest

interface CardSettingClickListener {
    fun onCardSettingClick(
        type: String,
        upDateCardSettingsRequest: UpDateCardSettingsRequest,
        fourDigitNumber: String? = null
    )
}