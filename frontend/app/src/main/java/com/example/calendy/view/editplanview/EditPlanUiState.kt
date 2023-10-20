package com.example.calendy.view.editplanview

import com.example.calendy.data.category.Category
import com.example.calendy.data.plan.Plan.PlanType
import java.util.Date

data class EditPlanUiState(
    val entryType: PlanType = PlanType.Schedule,
    val titleField: String = "",
    val memoField: String = "",
    val category: Category? = null,
    val priority: Int = 1,
    val startTime: Date = Date(),// schedule일 경우 필요
    val endTime: Date = Date(), //schedule 과  todo에 모두 필요
    val isComplete: Boolean = false,
    val showInMonthlyView: Boolean = true,
)
