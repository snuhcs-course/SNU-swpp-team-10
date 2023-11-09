package com.example.calendy.data.plan

sealed interface Plan {
    val id: Int
    val title: String
    val memo: String
    val categoryId: Int?
    val repeatGroupId: Int?
    val priority: Int?
    val showInMonthlyView: Boolean
    val isOverridden: Boolean

    sealed class PlanType {
        object Schedule : PlanType()
        object Todo: PlanType()
    }
}