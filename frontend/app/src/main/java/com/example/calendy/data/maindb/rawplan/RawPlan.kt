package com.example.calendy.data.maindb.rawplan

sealed interface RawPlan {
    val id: Int
    val title: String
    val memo: String
    val categoryId: Int?
    val repeatGroupId: Int?
    val priority: Int // if 0, raw-sql input
    val showInMonthlyView: Boolean
    val isOverridden: Boolean
}