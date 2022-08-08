package com.fypmoney.view.insights.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class MerchantCategoryResponse(
    @field:SerializedName("data")
    val category: List<MerchantCategory>,
)

@Keep
data class MerchantCategory(
    @field:SerializedName("category")
    var category: String,
    @field:SerializedName("iconLink")
    var iconLink: String,
    @field:SerializedName("categoryCode")
    var categoryCode: String,
)

@Keep
data class ChangeTxnCategory(
    var accReferenceNumber:String,
    var categoryCode:String
)

@Keep
data class MerchantCategoryUiModel(
    var categoryIcon:String,
    var categoryName:String,
    var categoryCode:String,
    var isSelected:Boolean,
){
    companion object{
        fun mapMerchantCategoryToMerchantCategoryUiModel(
            merchantCategory: MerchantCategory,
            categoryCode: String
        ):MerchantCategoryUiModel{
            return MerchantCategoryUiModel(
                categoryIcon = merchantCategory.iconLink,
                categoryName = merchantCategory.category,
                categoryCode = merchantCategory.categoryCode,
                isSelected = merchantCategory.categoryCode==categoryCode
            )
        }
    }
}