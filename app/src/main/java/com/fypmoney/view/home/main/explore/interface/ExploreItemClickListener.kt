package com.fypmoney.view.home.main.explore.`interface`

import com.fypmoney.view.home.main.explore.model.ExploreContentResponse
import com.fypmoney.view.home.main.explore.model.SectionContentItem

interface ExploreItemClickListener {
    fun onItemClicked(position: Int, sectionContentItem: SectionContentItem,
                      exploreContentResponse: ExploreContentResponse?=null)


}
