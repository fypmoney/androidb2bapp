package com.fypmoney.model


data class YourTaskModel(var Id: Int, var Relation: String, var Amount: String,
                         var Title: String?) {
    constructor() : this(0, "","","")
}