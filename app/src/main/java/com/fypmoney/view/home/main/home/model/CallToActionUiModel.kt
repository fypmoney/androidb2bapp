package com.fypmoney.view.home.main.home.model

import androidx.annotation.Keep

@Keep
data class CallToActionUiModel(
    var id:Int,
    var bannerImage:String?,
    var contentX:Int?,
    var contentY:Int?,
    var redirectionType:String?,
    var redirectionResource:String?

)
