package com.dreamfolks.fypmoney

import com.fypmoney.util.Utility
import org.junit.Test

import org.junit.Assert.*

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
    fun conevrt_strings() {
        var str = "ORDER PLACED"
        var out = Utility.firstCharCapitalOfString(str)
        assertEquals("Order Placed", out)
    }
}