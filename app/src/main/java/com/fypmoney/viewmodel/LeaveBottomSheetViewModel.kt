package com.fypmoney.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import com.fypmoney.base.BaseViewModel

class LeaveBottomSheetViewModel(application: Application) : BaseViewModel(application) {
    var name = ObservableField<String>()
}