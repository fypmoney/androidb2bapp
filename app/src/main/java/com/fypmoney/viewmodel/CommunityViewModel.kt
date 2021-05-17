package com.fypmoney.viewmodel

import android.app.Application
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseViewModel
import com.fypmoney.util.Utility

/*
* This class is used for handling community
* */
class CommunityViewModel(application: Application) : BaseViewModel(application) {
    var schoolName = MutableLiveData<String>()
    var cityName = MutableLiveData<String>()
    var onContinueClicked = MutableLiveData<Boolean>()

    /*
* This method is used to handle click of continue
* */
    fun onContinueClicked() {
        when {
            TextUtils.isEmpty(schoolName.value) -> {
                Utility.showToast(PockketApplication.instance.getString(R.string.school_name_empty_error))
            }
            TextUtils.isEmpty(cityName.value) -> {
                Utility.showToast(PockketApplication.instance.getString(R.string.city_name_empty_error))
            }

            else -> {
                onContinueClicked.value = true
            }
        }

    }

}