package com.example.calendy.utils

import com.example.calendy.utils.DateHelper.extract
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Date

class DateHelperTest {
    private val testDateExpected = Date(2023 - 1900, 10 - 1, 26, 16, 14)

    @Test
    fun getDate() {
        val expected = testDateExpected
        val result = DateHelper.getDate(
            year = 2023,
            monthZeroIndexed = 10 - 1,
            day = 26,
            hourOfDay = 16,
            minute = 14,
            assertValueIsValid = true
        )

        assertEquals(/* expected = */ expected, /* actual = */ result)
    }

    @Test
    fun getDate_whenInvalidDate() {
        val expected = Date(2023 - 1900, 2 - 1, 28, 16, 14)
        val result = DateHelper.getDate(
            year = 2023,
            monthZeroIndexed = 2 - 1,
            day = 31,
            hourOfDay = 16,
            minute = 14,
            assertValueIsValid = false
        )

        assertEquals(/* expected = */ expected, /* actual = */ result)
    }

    @Test
    fun getDateFromMillis() {
        val expected = testDateExpected
        val actual = DateHelper.getDateFromMillis(testDateExpected.time)

        assertEquals(/* expected = */ expected, /* actual = */ actual)
    }

    @Test
    fun extract() {
        val (year, monthZeroIndexed, day, hour, minute) = testDateExpected.extract()
        assertEquals(2023, year)
        assertEquals(9, monthZeroIndexed)
        assertEquals(26, day)
        assertEquals(16, hour)
        assertEquals(14, minute)
    }

    @Test
    fun endOf() {
        val expected = Date(2023 - 1900, 10 - 1, 31, 23, 59)
        val actual = DateHelper.endOf(2023, 10 - 1)

        assertEquals(/* expected = */ expected, /* actual = */ actual)
    }

    @Test
    fun getYearlyDueTime() {
    }

    @Test
    fun getMonthlyDueTime() {
    }

    @Test
    fun getDailyDueTime() {
    }

    @Test
    fun getDayOfWeek() {
    }
}