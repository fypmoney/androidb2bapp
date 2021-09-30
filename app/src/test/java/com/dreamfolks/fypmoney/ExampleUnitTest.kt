package com.dreamfolks.fypmoney

import com.fypmoney.util.Utility
import com.fypmoney.util.roundOfAmountToCeli
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
    fun round_off_amount() {
        val amountInPaisa = "355539"
        val amountInRuppes = Utility.convertToRs(amountInPaisa)
        val round = roundOfAmountToCeli(amountInRuppes!!)
        assertEquals(61, round)
    }
}