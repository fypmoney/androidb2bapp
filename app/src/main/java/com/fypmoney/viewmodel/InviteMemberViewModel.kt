package com.fypmoney.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel

/*
* This class is is used to invite a family member
* */
class InviteMemberViewModel(application: Application) : BaseViewModel(application) {
    var onInviteClicked = MutableLiveData<Boolean>()

    /*
    * This method is used to invite a family member
    * */
    fun onInviteClicked() {
        onInviteClicked.value = true


    }


}