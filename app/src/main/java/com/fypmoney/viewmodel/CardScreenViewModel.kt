package com.fypmoney.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseViewModel

class CardScreenViewModel(application: Application) : BaseViewModel(application)  {
    var balance =
        ObservableField(PockketApplication.instance.getString(R.string.dummy_amount))
    var name =
        ObservableField(PockketApplication.instance.getString(R.string.dummy_name))
}