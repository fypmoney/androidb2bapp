package com.fypmoney.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import com.fypmoney.base.BaseViewModel
import com.fypmoney.model.FeedDetails

/*
* This is used to show details
* */
class FeedDetailsViewModel(application: Application) : BaseViewModel(application) {
    var feedDetails = ObservableField<FeedDetails>()

}