package com.example.calendy.view.editplanview

import com.example.calendy.data.maindb.category.Category
import com.example.calendy.data.maindb.plan.PlanType
import com.example.calendy.data.maindb.repeatgroup.RepeatGroup
import java.util.Date

// default value is important for `initialize` in view model
data class EditPlanUiState(
    val isAddPage: Boolean, // true: add page. false: edit page
    val id: Int? = null, // id is set when editing existing plan. id is null if adding new plan
    // Style: properties' order is aligned with UI
    val entryType: PlanType = PlanType.TODO,
    val titleField: String = "",
    val isComplete: Boolean = false,
    val isAllDay: Boolean = false, // cannot specify time in Date Range Picker if true
    val startTime: Date = Date(),
    val endTime: Date = Date(),
    val isYearly: Boolean = false,
    val isMonthly: Boolean = false,
    val isDaily: Boolean = false,
    val dueTime: Date = Date(),
    val category: Category? = null,
    val repeatGroupId: Int? = null, // store 기존 repeat group. Needed when editing repeat settings.
    val repeatGroup: RepeatGroup? = null, // currently working repeat group. This repeat group can be changed in Edit Page
    val priority: Int = 1,
    val memoField: String = "",
    val showInMonthlyView: Boolean = true,
)
