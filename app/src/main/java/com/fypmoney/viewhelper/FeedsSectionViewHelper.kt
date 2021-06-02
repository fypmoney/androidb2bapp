package com.fypmoney.viewhelper

import androidx.databinding.ObservableField
import com.fypmoney.model.FeedDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.view.adapter.FeedsAdapter
import com.fypmoney.view.adapter.FeedsSectionAdapter

/*
* This is used to display all the feeds in the list
* */
class FeedsSectionViewHelper(
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