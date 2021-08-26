package com.fypmoney.model
import androidx.annotation.Keep



@Keep
data class SettingsRequest(
    var keyList: List<String> = listOf()
)
@Keep
data class SettingsResponse(
    val `data`: Data
)

@Keep
data class Data(
    val keyList: List<String>,
    val keyNotFoundList: List<Any>,
    val keysFound: List<KeysFound>
)

@Keep
data class KeysFound(
    val description: String,
    val key: String,
    val lastModifiedData: String,
    val name: String,
    val value: String
)