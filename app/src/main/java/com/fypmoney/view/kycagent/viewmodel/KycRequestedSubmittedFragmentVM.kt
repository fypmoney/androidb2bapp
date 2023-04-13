package com.fypmoney.view.kycagent.viewmodel

import android.app.Application
import com.fypmoney.base.BaseViewModel

class KycRequestedSubmittedFragmentVM(application: Application) : BaseViewModel(application) {

    lateinit var via: String
    var message : String = "Your KYC Details have been successfully submitted."

}