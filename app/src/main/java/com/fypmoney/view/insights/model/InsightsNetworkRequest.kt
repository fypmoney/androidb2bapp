package com.fypmoney.view.insights.model

import androidx.annotation.Keep
import com.fypmoney.model.BaseRequest


@Keep
data class InsightsNetworkRequest(
    var startDate:String?,
    var endDate:String?,
): BaseRequest()
