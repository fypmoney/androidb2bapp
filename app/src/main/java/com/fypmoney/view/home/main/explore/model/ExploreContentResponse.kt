package com.fypmoney.view.home.main.explore.model

data class ExploreContentResponse(
    val sectionSortOrder: Int? = null,
    val actionCardFlag: String? = null,
    val redirectionType: String? = null,
    val sectionContent: List<SectionContentItem?>? = null,
    val showMore: String? = null,
    val showMoreRedirectionResource: String? = null,
    val sectionCode: String? = null,
    val id: Int? = null,
    val sectionDisplayText: String? = null,
    val status: String? = null
)

data class SectionContentItem(
    val contentResourceUri: String? = null,
    val redirectionResource: String? = null,
    val redirectionType: String? = null,
    val sortOrder: Int? = null,
    val contentDimensionX: Int? = null,
    val id: Any? = null,
    val contentType: String? = null,
    val contentDimensionY: Int? = null,
    val status: String? = null
)

