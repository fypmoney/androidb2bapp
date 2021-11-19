package com.fyp.trackr

import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*
const val SERVER_DATE_TIME_FORMAT1 = "yyyy-MM-dd'T'HH:mm:ss'Z'"
const val CHANGED_DATE_TIME_FORMAT5 = " dd-MM-yyyy"

fun parseDateStringIntoDate(
    dateTime: String? = null,
    inputFormat: String? = SERVER_DATE_TIME_FORMAT1,
    outputFormat: String? = CHANGED_DATE_TIME_FORMAT5
): Date? {
    return if (dateTime != null) {
        val input = SimpleDateFormat(inputFormat, Locale.getDefault())
        val output = SimpleDateFormat(outputFormat, Locale.getDefault())
        output.timeZone = TimeZone.getTimeZone("UTC")
        var d: Date? = null
        try {
            d = input.parse(dateTime)
            d = output.parse(d.toString())
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return removeTime(d)
    } else {
        null
    }
}

fun removeTime(date: Date?): Date? {
    val cal = Calendar.getInstance()
    cal.time = date
    cal[Calendar.HOUR_OF_DAY] = 0
    cal[Calendar.MINUTE] = 0
    cal[Calendar.SECOND] = 0
    cal[Calendar.MILLISECOND] = 0
    return cal.time
}
