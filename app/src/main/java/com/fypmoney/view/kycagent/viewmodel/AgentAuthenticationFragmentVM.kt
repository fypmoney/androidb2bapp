package com.fypmoney.view.kycagent.viewmodel

import android.app.Application
import com.fypmoney.base.BaseViewModel

class AgentAuthenticationFragmentVM(application: Application) : BaseViewModel(application) {

    var mobileNumber: String ?= null
    lateinit var via: String
    var aadhaarNumber : String ?= null

}