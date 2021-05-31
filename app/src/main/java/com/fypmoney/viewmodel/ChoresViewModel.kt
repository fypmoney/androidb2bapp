package com.fypmoney.viewmodel

import android.app.Application
import android.text.TextUtils
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseViewModel
import com.fypmoney.database.entity.MemberEntity
import com.fypmoney.database.entity.TaskEntity
import com.fypmoney.util.Utility

/*
* This class is used for handling chores
* */
class ChoresViewModel(application: Application) : BaseViewModel(application) {
    var onAddMoneyClicked = MutableLiveData(false)
    var taskDetailsData = ObservableField<TaskEntity>()

    /*
* This method is used to handle click of continue
* */
    /*
    * This will handle the click of select
    * */
    fun onSelectClicked() {
        onAddMoneyClicked.value = true
    }

}