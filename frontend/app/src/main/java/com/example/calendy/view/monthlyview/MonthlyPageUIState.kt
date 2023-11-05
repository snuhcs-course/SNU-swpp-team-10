package com.example.calendy.view.monthlyview

import com.example.calendy.data.plan.Plan
import com.prolificinteractive.materialcalendarview.CalendarDay
import java.util.Hashtable

data class MonthlyPageUIState (
    val selectedDate : CalendarDay = CalendarDay.today(),
    val plansOfMonth : List<Plan> = emptyList(),
    val currentMonth:CalendarDay = CalendarDay.today()
)