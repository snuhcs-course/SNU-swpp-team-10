package com.example.calendy.data.maindb.plan

sealed interface Plan {
    val id: Int
    val title: String
    val memo: String
    val categoryId: Int?
    val priority: Int
    val showInMonthlyView: Boolean
    val repeatGroupId: Int?
    val isOverridden: Boolean

    companion object {
        const val PRIORITY_DEFAULT = 3
    }
}