package com.fypmoney.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import com.fypmoney.base.BaseViewModel
import com.fypmoney.database.LogRepository

/*
* This is used to handle user profile
* */
class UserProfileViewModel(application: Application) : BaseViewModel(application) {
    var userName = ObservableField<String>()
    val logRepository = LogRepository(appDatabase)


}