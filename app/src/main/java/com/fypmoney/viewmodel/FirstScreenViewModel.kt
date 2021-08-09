package com.fypmoney.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.util.livedata.LiveEvent

/*
* This class is first screen using which account can be login or created
* */
class FirstScreenViewModel(application: Application) : BaseViewModel(application) {
    val event: LiveData<FirstScreenEvent>
        get() = _event
    private var _event = LiveEvent<FirstScreenEvent>()

    /*
    * This method is used to handle create account
    * */
    fun onCreateAccountClicked() {
        _event.value = FirstScreenEvent.CreateAccount
    }
    fun onBackToLoginClicked() {
        _event.value = FirstScreenEvent.BackToLogin
    }

    sealed class FirstScreenEvent{
        object CreateAccount:FirstScreenEvent()
        object BackToLogin:FirstScreenEvent()
    }

}