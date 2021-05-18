package com.fypmoney.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import com.fypmoney.base.BaseViewModel

/*
* This class is is used to display that request sent to add a family member
* */
class StayTunedViewModel(application: Application) : BaseViewModel(application) {
    var relation = ObservableField<String>()


}