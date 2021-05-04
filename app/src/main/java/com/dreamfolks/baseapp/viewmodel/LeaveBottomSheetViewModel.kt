package com.dreamfolks.baseapp.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import com.dreamfolks.baseapp.base.BaseViewModel

class LeaveBottomSheetViewModel(application: Application) : BaseViewModel(application) {
    var name = ObservableField<String>()
}