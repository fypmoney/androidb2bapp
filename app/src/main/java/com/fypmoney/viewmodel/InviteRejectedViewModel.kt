package com.fypmoney.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel

/*
* This class is is used to display message in case invite rejected by the parent
* */
class InviteRejectedViewModel(application: Application) : BaseViewModel(application) {
    var onRequestAgainClicked = MutableLiveData<Boolean>()

    /*
    * This method is used to request again a family member
    * */
    fun onRequestAgainClicked() {
        onRequestAgainClicked.value = true


    }


}