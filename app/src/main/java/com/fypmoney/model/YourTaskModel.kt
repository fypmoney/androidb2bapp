package com.fypmoney.model

import androidx.annotation.Keep

@Keep
data class YourTaskModel(var Id: Int, var Relation: String, var Amount: String,
                         var Title: String?) {
    constructor() : this(0, "","","")
}