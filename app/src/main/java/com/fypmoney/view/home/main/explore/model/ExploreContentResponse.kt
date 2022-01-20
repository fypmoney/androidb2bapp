package com.fypmoney.view.home.main.explore.model

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.Keep

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
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.createTypedArrayList(SectionContentItem),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(sectionSortOrder)
        parcel.writeString(actionCardFlag)
        parcel.writeString(redirectionType)
        parcel.writeTypedList(sectionContent)
        parcel.writeString(showMore)
        parcel.writeString(showMoreRedirectionResource)
        parcel.writeString(sectionCode)
        parcel.writeValue(id)
        parcel.writeString(sectionDisplayText)
        parcel.writeString(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ExploreContentResponse> {
        override fun createFromParcel(parcel: Parcel): ExploreContentResponse {
            return ExploreContentResponse(parcel)
        }

        override fun newArray(size: Int): Array<ExploreContentResponse?> {
            return arrayOfNulls(size)
        }
    }
}

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
    val status: String? = null
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(contentResourceUri)
        parcel.writeString(redirectionResource)
        parcel.writeString(redirectionType)
        parcel.writeValue(sortOrder)
        parcel.writeValue(contentDimensionX)
        parcel.writeValue(id)
        parcel.writeString(contentType)
        parcel.writeValue(contentDimensionY)
        parcel.writeString(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SectionContentItem> {
        override fun createFromParcel(parcel: Parcel): SectionContentItem {
            return SectionContentItem(parcel)
        }

        override fun newArray(size: Int): Array<SectionContentItem?> {
            return arrayOfNulls(size)
        }
    }
}

