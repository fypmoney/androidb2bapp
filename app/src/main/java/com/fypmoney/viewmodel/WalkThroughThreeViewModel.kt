package com.fypmoney.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel

/*
* This is a third walk through screen
* */
class WalkThroughThreeViewModel(application: Application) : BaseViewModel(application) {
    var onSkipClicked = MutableLiveData<Boolean>()


    /*
    * This is used handle skip
    * */
    fun onSkipClicked() {
        onSkipClicked.value=true

    }
}