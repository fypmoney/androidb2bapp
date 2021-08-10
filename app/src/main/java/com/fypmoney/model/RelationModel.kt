package com.fypmoney.model

data class RelationModel(
    var relationName: String? = null,
    var relationImage: Int? = null,
    var isSelected: Boolean? = false,
    var pos: Int = 0
) {
}