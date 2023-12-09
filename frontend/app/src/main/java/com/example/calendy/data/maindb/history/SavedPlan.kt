package com.example.calendy.data.maindb.history

sealed interface SavedPlan {
    val id: Int
    val title: String
    val memo: String
    val categoryId: Int?
    val repeatGroupId: Int?
    val priority: Int
    val showInMonthlyView: Boolean
    val isOverridden: Boolean
}