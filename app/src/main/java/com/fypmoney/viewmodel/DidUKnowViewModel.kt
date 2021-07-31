package com.fypmoney.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import com.fypmoney.base.BaseViewModel

class DidUKnowViewModel(application: Application) : BaseViewModel(application) {
    var resourceId = ObservableField<ArrayList<String?>>()
    var position = ObservableField(0)
    var imageUrl = ObservableField<String>()
}