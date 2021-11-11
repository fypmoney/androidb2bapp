package com.fyp.trackr

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
const val SERVER_DATE_TIME_FORMAT1 = "yyyy-MM-dd'T'HH:mm:ss'Z'"

fun parseDateStringIntoDate(
    dateTime: String? = null,
    inputFormat: String? = SERVER_DATE_TIME_FORMAT1,
): Date? {
    return if (dateTime != null) {
        val input = SimpleDateFormat(inputFormat, Locale.getDefault())

        var d: Date? = null
        try {
            d = input.parse(dateTime)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return d
    } else {
        null
    }
}
