package com.dreamfolks.fypmoney

import com.fypmoney.util.AppConstants.CHANGED_DATE_TIME_FORMAT5
import com.fypmoney.util.Utility
import com.fypmoney.util.roundOfAmountToCeli
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.roundToInt

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
    @Test
    fun format_string_to_card_number() {
        val cardNumber = "4456970100000228"
        val formattedCardNumber = Utility.stringToCardNumber(cardNumber)
        assertEquals(StringBuilder("4456 9701 0000 0228"), formattedCardNumber)
    }

    @Test
    fun numberFormatTest(){
        println(("₹116.01".split("₹")[1]).toDouble().roundToInt())
    }


    @Test
    fun getStartAndEndDate(){
        val dates = Utility.getStartDateAndEndDateOfMonth(0,CHANGED_DATE_TIME_FORMAT5)
        System.out.println("pairs ${dates.first} ${dates.second}")
    }

    @Test
    fun getLast12Months(){
        val list = Utility.getLast12Months("Apr")
        println("final list ${list}")
    }
}