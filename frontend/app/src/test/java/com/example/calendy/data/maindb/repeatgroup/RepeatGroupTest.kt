package com.example.calendy.data.maindb.repeatgroup

import com.example.calendy.utils.DateHelper
import com.example.calendy.utils.DateHelper.extract
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
        repeatGroup.toIterable(DateHelper.getDate(2023, 11 - 1, 7)).forEach {
            val (year, month, day, _, _) = it.extract()
            val repeatedDate = it.applyTime(20, 56)
            if (repeatedDate < it) {
                println("${it.toString()} -> ${repeatedDate.toString()}")
            }
        }
    }
}