package com.example.calendy.view.monthlyview

import com.example.calendy.data.maindb.plan.PlanType
import com.example.calendy.data.maindb.plan.Schedule
import com.example.calendy.data.maindb.plan.Todo
import java.util.Date

data class DayPlanDetailUiState(
    val item: DayPlanDetailItem? = null,
)
data class DayPlanDetailItem(
    val planType: PlanType = PlanType.SCHEDULE,
    val title: String = "",
    val startTime: Date = Date(),// schedule일 경우 필요
    val endTime: Date = Date(), //schedule 과  todo에 모두 필요
    val memo: String = "",
    val location: String = "",
    val isComplete: Boolean = false,
    val showInMonthlyView: Boolean = true
)

fun Schedule.toDetailItem() : DayPlanDetailItem = DayPlanDetailItem(
    planType = if(this.javaClass == Schedule::class.java) {
        com.example.calendy.data.maindb.plan.PlanType.SCHEDULE} else {
        com.example.calendy.data.maindb.plan.PlanType.TODO},
    title = title,
    startTime = startTime,
    endTime = endTime,
    isComplete = if(this.javaClass == Todo::class.java) (this as Todo).complete else false,
    showInMonthlyView = showInMonthlyView
)