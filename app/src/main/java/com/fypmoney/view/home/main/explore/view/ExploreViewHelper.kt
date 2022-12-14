package com.fypmoney.view.home.main.explore.view

import android.view.View
import androidx.databinding.ObservableField
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility.hapticFeedback
import com.fypmoney.view.home.main.explore.`interface`.ExploreItemClickListener
import com.fypmoney.view.home.main.explore.model.ExploreContentResponse
import com.fypmoney.view.home.main.explore.model.SectionContentItem

/*
* This is used to display all the feeds in the list
* */
class ExploreViewHelper(
    var view: View,
    var position: Int,
    var oneSection: SectionContentItem?,
    var onFeedItemClickListener: ExploreItemClickListener,
    var type: Int? = 0,
    var exploreContentResponse: ExploreContentResponse
) {
    var isTitleVisible = ObservableField(true)

    init {

        when (oneSection?.contentType) {
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
        view.hapticFeedback()
        onFeedItemClickListener.onItemClicked(position, oneSection!!,exploreContentResponse)

    }


}