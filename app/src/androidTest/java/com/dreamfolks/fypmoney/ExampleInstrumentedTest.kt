package com.dreamfolks.fypmoney

import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fypmoney.util.Utility

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.dreamfolks.baseapp", appContext.packageName)
    }

    @Test
    fun convertDateFormat_MoengageDashboard(){
        var dob = "1993-11-12T00:00:00Z"
        var inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val date = inputFormat.parse(dob)
        Log.d("date","$date")
        val formattedDate = SimpleDateFormat("yyyy-MM-dd").format(date)
        Log.d("formattedDate","$formattedDate")
        assertEquals("1993-11-12", formattedDate)
    }
}