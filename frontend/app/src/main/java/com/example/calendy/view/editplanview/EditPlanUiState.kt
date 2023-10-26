package com.example.calendy.view.editplanview

import com.example.calendy.data.category.Category
import com.example.calendy.data.plan.Plan.PlanType
import com.example.calendy.data.repeatgroup.RepeatGroup
import java.util.Date

// default value is important for `initialize` in view model
data class EditPlanUiState(
    val isAddPage: Boolean, // true: add page. false: edit page
    val id: Int? = null, // id is set when editing existing plan
    // Style: properties' order is aligned with UI
    val entryType: PlanType = PlanType.Todo,
    val titleField: String = "",
    val isComplete: Boolean = false,
    val startTime: Date = Date(),
    val endTime: Date = Date(),
    val isYearly: Boolean = false,
    val isMonthly: Boolean = false,
    val isDaily: Boolean = false,
    val dueTime: Date = Date(),
    val category: Category? = null,
    val repeatGroup: RepeatGroup? = null,
    val priority: Int = 1,
    val memoField: String = "",
    val showInMonthlyView: Boolean = true,
)
