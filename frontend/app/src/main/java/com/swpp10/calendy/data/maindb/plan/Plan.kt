package com.swpp10.calendy.data.maindb.plan

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

    fun toSummary(getCategoryTitle: (Int?) -> String): String {
        val (planType, uniqueSummary) = typedSummary()
        val categoryTitle = getCategoryTitle(categoryId)
        return "$planType(title=$title, $uniqueSummary, memo=$memo, category=$categoryTitle, priority=$priority)"
        // "Schedule(title, startTime, endTime, memo, category, priority)"
        // "Tod0(title, dueTime, complete, memo, category, priority)"
    }

    fun typedSummary(): Pair<String, String>

}