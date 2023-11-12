package com.example.calendy.view.monthlyview

import com.example.calendy.data.maindb.plan.PlanType
import com.example.calendy.data.maindb.plan.Schedule
import com.example.calendy.data.maindb.plan.Todo
import java.util.Date

data class DayPlanListUiState(
    val day : Int?=1,
    val dayText : String?="월",  //need to bind with non-static string value
    val plans: ArrayList<DayPlanListItem>?=null
)
data class DayPlanListItem(
    val planType: PlanType = PlanType.SCHEDULE,
    val titleField: String = "",
    val startTime: Date = Date(),// schedule일 경우 필요
    val endTime: Date = Date(), //schedule 과  todo에 모두 필요
    val isComplete: Boolean = false,
    val showInMonthlyView: Boolean = true,
)

fun Schedule.toListItem() : DayPlanListItem = DayPlanListItem(
    planType = if(this.javaClass == Schedule::class.java) {
        com.example.calendy.data.maindb.plan.PlanType.SCHEDULE} else {
        com.example.calendy.data.maindb.plan.PlanType.TODO},
    titleField = title,
    startTime = startTime,
    endTime = endTime,
    isComplete = if(this.javaClass == Todo::class.java) (this as Todo).complete else false,
    showInMonthlyView = showInMonthlyView
)