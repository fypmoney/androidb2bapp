package com.fypmoney.viewmodel

import android.app.Application
import com.fypmoney.base.BaseViewModel
import com.fypmoney.database.LogRepository

class LogViewModel(application: Application) : BaseViewModel(application) {
    val logRepository = LogRepository(appDatabase)
}