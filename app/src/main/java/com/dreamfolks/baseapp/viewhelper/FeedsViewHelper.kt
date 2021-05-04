package com.dreamfolks.baseapp.viewhelper

import androidx.databinding.ObservableField
import com.dreamfolks.baseapp.model.FeedDetails
import com.dreamfolks.baseapp.model.FeedResponseModel
import com.dreamfolks.baseapp.util.AppConstants
import com.dreamfolks.baseapp.view.adapter.FeedsAdapter

/*
* This is used to display all the feeds in the list
* */
class FeedsViewHelper(
    var feedDetails: FeedDetails?,
    var onFeedItemClickListener: FeedsAdapter.OnFeedItemClickListener
) {
    var isButtonVisible = ObservableField(true)

    init {
        when (feedDetails?.action?.type) {
            AppConstants.FEED_TYPE_NONE-> {
                isButtonVisible.set(false)
            }

        }

    }

    /*
    * This is used to handle button click
    * */
    fun onFeedButtonClick() {
        onFeedItemClickListener.onFeedClick(feedDetails!!)

    }


}