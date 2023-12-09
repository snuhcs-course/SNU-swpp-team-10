package com.example.calendy.data.maindb.history

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.calendy.data.maindb.plan.Schedule
import java.util.Date


@Entity(
    tableName = "saved_schedule"
)
data class SavedSchedule(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    override val id: Int = 0,
    @ColumnInfo(name = "title")
    override val title: String,
    @ColumnInfo(name = "start_time")
    val startTime: Date,
    @ColumnInfo(name = "end_time")
    val endTime: Date,
    @ColumnInfo(name = "memo")
    override val memo: String = "",
    @ColumnInfo(name = "repeat_group_id")
    override val repeatGroupId: Int?,
    @ColumnInfo(name = "category_id")
    override val categoryId: Int?,
    @ColumnInfo(name = "priority")
    override val priority: Int,
    @ColumnInfo(name = "show_in_monthly_view")
    override val showInMonthlyView: Boolean,
    @ColumnInfo(name = "is_overridden")
    override val isOverridden: Boolean
) : SavedPlan {
    fun toSchedule(): Schedule = Schedule(
        id = 0,
        title = title,
        startTime = startTime,
        endTime = endTime,
        memo = memo,
        repeatGroupId = repeatGroupId,
        categoryId = categoryId,
        priority = priority,
        showInMonthlyView = showInMonthlyView,
        isOverridden = isOverridden
    )
}

fun Schedule.toSavedSchedule() = SavedSchedule(
    id = 0,
    title = title,
    startTime = startTime,
    endTime = endTime,
    memo = memo,
    repeatGroupId = repeatGroupId,
    categoryId = categoryId,
    priority = priority,
    showInMonthlyView = showInMonthlyView,
    isOverridden = isOverridden
)