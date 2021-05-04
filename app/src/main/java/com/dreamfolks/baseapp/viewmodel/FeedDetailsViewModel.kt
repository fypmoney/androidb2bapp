package com.dreamfolks.baseapp.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import com.dreamfolks.baseapp.base.BaseViewModel
import com.dreamfolks.baseapp.model.FeedDetails

/*
* This is used to show details
* */
class FeedDetailsViewModel(application: Application) : BaseViewModel(application) {
    var feedDetails = ObservableField<FeedDetails>()

}