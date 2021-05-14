package com.fypmoney.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel

/*
* This class is first screen using which account can be login or created
* */
class FirstScreenViewModel(application: Application) : BaseViewModel(application) {
    var onCreateAccountClicked = MutableLiveData<Boolean>()
    var isLogoVisible = ObservableField(true)


    /*
    * This method is used to handle create account
    * */
    fun onCreateAccountClicked() {
        onCreateAccountClicked.value=true
    }

}