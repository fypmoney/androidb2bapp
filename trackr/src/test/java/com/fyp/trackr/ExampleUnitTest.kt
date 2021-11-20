package com.fyp.trackr

import org.junit.Test

import org.junit.Assert.*
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun dateOfBirthday(){
        var dobAsString = "1993-11-12T00:00:00Z"
        val dob = parseDateStringIntoDate(dobAsString,
            inputFormat = SERVER_DATE_TIME_FORMAT1)
        assertEquals("12-11-2021", dob)
    }
    @Test
    fun currentDate(){
        val date = currentFormattedDate()
        assertEquals("12-11-2021", date)
    }
}