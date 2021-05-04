package com.dreamfolks.baseapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.dreamfolks.baseapp.R
import com.dreamfolks.baseapp.base.BaseViewModel

/*
* This is used to handle user profile
* */
class UserProfileViewModel(application: Application) : BaseViewModel(application) {
    var userName = ObservableField<String>()




}