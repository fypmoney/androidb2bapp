package com.fypmoney.viewhelper

import androidx.databinding.ObservableField
import com.fypmoney.model.FeedDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.view.adapter.FeedsAdapter

/*
* This is used to display all the feeds in the list
* */
class FeedsViewHelper(
    var position: Int,
    var feedDetails: FeedDetails?,
    var onFeedItemClickListener: FeedsAdapter.OnFeedItemClickListener,var type:Int?=0
) {
    var isButtonVisible = ObservableField(true)
    var isTitleVisible = ObservableField(true)

    init {

        when (feedDetails?.displayCard) {
            AppConstants.FEED_TYPE_BLOG -> {
                isTitleVisible.set(true)
            }
            else->
            {
                isTitleVisible.set(false)
            }
        }

    }

    /*
    * This is used to handle button click
    * */
    fun onFeedButtonClick() {
        onFeedItemClickListener.onFeedClick(position, feedDetails!!)

    }


}