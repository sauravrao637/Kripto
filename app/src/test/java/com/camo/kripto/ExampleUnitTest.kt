package com.camo.kripto

import com.camo.kripto.utils.Extras
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
    fun formatter(){
        println(Extras.getFormattedDoubleCurr(-1983793712310.985739570, "inr", suffix = ""))
    }
}