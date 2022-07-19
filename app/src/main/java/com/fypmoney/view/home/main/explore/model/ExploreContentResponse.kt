package com.fypmoney.view.home.main.explore.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Parcelize
@Keep
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
):Parcelable

@Parcelize
@Keep
data class SectionContentItem(
    val contentResourceUri: String? = null,
    val redirectionResource: String? = null,
    val redirectionType: String? = null,
    val sortOrder: Int? = null,
    var contentDimensionX: Int? = null,
    val id: Int? = null,
    var contentType: String? = null,
    var actionFlagCode: String? = null,
    var contentDimensionY: Int? = null,
    val status: String? = null,
    val rfu1: String? = null,
    val rfu2: String? = null,
):Parcelable

