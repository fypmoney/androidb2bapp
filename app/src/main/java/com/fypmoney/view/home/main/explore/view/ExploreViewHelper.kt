package com.fypmoney.view.home.main.explore.view

import androidx.databinding.ObservableField
import com.fypmoney.model.FeedDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.view.adapter.FeedsAdapter
import com.fypmoney.view.home.main.explore.`interface`.ExploreItemClickListener
import com.fypmoney.view.home.main.explore.adapters.ExploreAdapter
import com.fypmoney.view.home.main.explore.model.SectionContentItem

/*
* This is used to display all the feeds in the list
* */
class ExploreViewHelper(
    var position: Int,
    var feedDetails: SectionContentItem?,
    var onFeedItemClickListener: ExploreItemClickListener, var type: Int? = 0
) {
    var isButtonVisible = ObservableField(true)
    var isTitleVisible = ObservableField(true)

    init {

        when (feedDetails?.contentType) {
            AppConstants.FEED_TYPE_BLOG -> {
                isTitleVisible.set(true)
            }
            else -> {
                isTitleVisible.set(false)
            }
        }

    }

    /*
    * This is used to handle button click
    * */
    fun onFeedButtonClick() {
        onFeedItemClickListener.onItemClicked(position, feedDetails!!)

    }


}