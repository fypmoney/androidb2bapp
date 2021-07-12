package com.fypmoney.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel

/*
* This is a main walk through screen
* */
class WalkThroughMainViewModel(application: Application) : BaseViewModel(application) {
    var onMainClicked = MutableLiveData<Boolean>()


    /*
    * This is used handle next button
    * */
    fun onMainClicked() {
        onMainClicked.value=true

    }
}