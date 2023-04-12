package com.fypmoney.view.kycagent.viewmodel

import android.app.Application
import com.fypmoney.base.BaseViewModel

class EnterAadhaarNumberKycFragmentVM(application: Application) : BaseViewModel(application) {

    var mobileNumber: String ?= null
    lateinit var via: String
    var lastFourDigitCode : String ?= null

}