package com.example.calendy.view.monthlyview

import com.example.calendy.R
import com.example.calendy.data.PlanType
import com.example.calendy.data.category.Category
import com.example.calendy.data.plan.Plan
import com.example.calendy.data.plan.Schedule
import com.example.calendy.data.plan.Todo
import java.util.Date
import kotlin.reflect.typeOf

data class DayPlanListUiState(
    val day : Int?=1,
    val dayText : String?="월",  //need to bind with non-static string value
    val plans: ArrayList<DayPlanListItem>?=null
)
data class DayPlanListItem(
    val planType: Plan.PlanType = Plan.PlanType.Schedule,
    val titleField: String = "",
    val startTime: Date = Date(),// schedule일 경우 필요
    val endTime: Date = Date(), //schedule 과  todo에 모두 필요
    val isComplete: Boolean = false,
    val showInMonthlyView: Boolean = true,
)

//fun Schedule.toListItem() : DayPlanListItem = DayPlanListItem(
//    planType = if(this.javaClass == Schedule::class.java) {Plan.PlanType.Schedule} else {Plan.PlanType.Todo},
//    titleField = title,
//    startTime = startTime,
//    endTime = endTime,
//    isComplete = if(this.javaClass == Todo::class.java) (this as Todo).complete else false,
//    showInMonthlyView = showInMonthlyView
//)