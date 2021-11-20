package com.fyp.trackr

import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*
const val SERVER_DATE_TIME_FORMAT1 = "yyyy-MM-dd'T'HH:mm:ss'Z'"
const val CHANGED_DATE_TIME_FORMAT5 = "dd-MM-yyyy"
const val CHANGED_DATE_TIME_FORMAT6 = "yyyy-MM-dd HH:mm:ss"

fun parseDateStringIntoDate(
    dateTime: String? = null,
    inputFormat: String? = SERVER_DATE_TIME_FORMAT1,
    outputFormat: String? = CHANGED_DATE_TIME_FORMAT5
): Date? {

    return if (dateTime != null) {
        val input = SimpleDateFormat(inputFormat)
        input.timeZone = TimeZone.getTimeZone("GMT")
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
fun currentFormattedDate():Date?{
    val date = Date()
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val dateInString = sdf.format(date)
    var d: Date? = null
    try {
        sdf.timeZone = TimeZone.getTimeZone("IST")
        d = sdf.parse(dateInString)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return d
}

