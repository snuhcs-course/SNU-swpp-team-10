package com.example.calendy.view.editplanview

import com.example.calendy.data.category.Category
import com.example.calendy.data.plan.Plan.PlanType
import java.util.Date

data class EditPlanUiState(
    // Style: properties' order is aligned with UI
    val entryType: PlanType = PlanType.Schedule,
    val titleField: String = "",
    val isComplete: Boolean = false,
    val startTime: Date = Date(),// schedule일 경우 필요
    val endTime: Date = Date(), //schedule 과  todo에 모두 필요,
    val isYearly: Boolean = false,
    val isMonthly: Boolean = false,
    val isDaily: Boolean = false,
    val dueTime: Date = Date(),
    val category: Category? = null,
    val priority: Int = 1,
    val memoField: String = "",
    val showInMonthlyView: Boolean = true,
)
