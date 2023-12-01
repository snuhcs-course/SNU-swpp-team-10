package com.example.calendy.data.maindb.repeatgroup

import com.example.calendy.utils.DateHelper
import com.example.calendy.utils.applyTime
import org.junit.Test

class RepeatGroupTest {
    @Test
    fun toIterable() {
        val repeatGroup = RepeatGroup(
            day = false,
            week = true,
            month = false,
            year = false,
            repeatInterval = 1,
            repeatRule = "MONWEDFRI",
            endDate = null
        )

        var previousDate = DateHelper.getDate(2023, 11 - 1, 5)

        for (startDateOnly in repeatGroup.toIterable()) {
            // Iterator returns Date Only without time information.
            val repeatedDate = startDateOnly.applyTime(10, 56)
            if (!(previousDate < repeatedDate)) {
                println("Previous: $previousDate -> Current: $repeatedDate")
            } else if (repeatedDate.hours != 10 || repeatedDate.minutes != 56) {
                println("Current: $repeatedDate")
            }
            previousDate = repeatedDate
        }
    }
}