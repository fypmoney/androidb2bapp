package com.fypmoney.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable
import java.lang.reflect.Array
@Keep
data class FeedRequestModel(
    @SerializedName("query") var query: String? = null
) : BaseRequest()

@Keep
data class FeedResponseModel(
    @SerializedName("data") var getAllFeed: FeedResponseModelDetails?,
) : Serializable
@Keep
data class FeedResponseModelDetails(
    @SerializedName("getAllFeed") var getAllFeed: GetAllFeed2?,
) : Serializable

@Keep
data class GetAllFeed2(
    @SerializedName("total") var total: Int? = 0,
    @SerializedName("feedData") var feedDetails: List<FeedDetails>?
) : Serializable

@Keep
data class FeedDetails(
    @SerializedName("id") var id: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("screenName") var screenName: String? = null,
    @SerializedName("screenSection") var screenSection: String? = null,
    @SerializedName("displayCard") var displayCard: String? = null,
    @SerializedName("createdDate") var createdDate: String? = null,
    @SerializedName("author") var author: String? = null,
    @SerializedName("readTime") var readTime: String? = null,
    @SerializedName("category") var category: Category? = null,
    @SerializedName("location") var location: Location? = null,
    @SerializedName("backgroundColor") var backgroundColor: String? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("subTitle") var subTitle: String? = null,
    @SerializedName("responsiveContent") var responsiveContent: String? = null,
    @SerializedName("resourceId") var resourceId: String? = null,
    @SerializedName("action") var action: Action? = null,
    @SerializedName("scope") var scope: String?,
    @SerializedName("resourceArr") var resourceArr: List<String> = arrayListOf()
) : Serializable
@Keep
data class Action(
    @SerializedName("url") var url: String,
    @SerializedName("type") var type: String?,
    @SerializedName("buttonText") var buttonText: String?,
) : Serializable
@Keep
data class Category(
    @SerializedName("name") var name: String?,
    @SerializedName("code") var code: String?,
    @SerializedName("description") var description: String?
) : Serializable
@Keep
data class Location(
    @SerializedName("type") var type: String?,
    @SerializedName("coordinates") var coordinates: ArrayList<String>?,
) : Serializable


