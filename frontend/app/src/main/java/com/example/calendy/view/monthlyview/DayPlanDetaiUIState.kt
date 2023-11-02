package com.example.calendy.view.monthlyview

import com.example.calendy.R
import com.example.calendy.data.PlanType
import com.example.calendy.data.category.Category
import com.example.calendy.data.plan.Plan
import com.example.calendy.data.plan.Schedule
import com.example.calendy.data.plan.Todo
import java.util.Date
import kotlin.reflect.typeOf

data class DayPlanDetailUiState(
    val item: DayPlanDetailItem? = null,
)
data class DayPlanDetailItem(
    val planType: Plan.PlanType = Plan.PlanType.Schedule,
    val title: String = "",
    val startTime: Date = Date(),// schedule일 경우 필요
    val endTime: Date = Date(), //schedule 과  todo에 모두 필요
    val memo: String = "",
    val location: String = "",
    val isComplete: Boolean = false,
    val showInMonthlyView: Boolean = true
)

fun Schedule.toDetailItem() : DayPlanDetailItem = DayPlanDetailItem(
    planType = if(this.javaClass == Schedule::class.java) {Plan.PlanType.Schedule} else {Plan.PlanType.Todo},
    title = title,
    startTime = startTime,
    endTime = endTime,
    isComplete = if(this.javaClass == Todo::class.java) (this as Todo).complete else false,
    showInMonthlyView = showInMonthlyView
)