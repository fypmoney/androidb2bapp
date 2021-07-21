package com.fypmoney.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import com.fypmoney.base.BaseViewModel

class DidUKnowViewModel(application: Application) : BaseViewModel(application) {
    var resourceId = ObservableField<String>()
}