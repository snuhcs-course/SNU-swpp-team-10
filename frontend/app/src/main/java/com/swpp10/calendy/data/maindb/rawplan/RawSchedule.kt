package com.swpp10.calendy.data.maindb.rawplan

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.swpp10.calendy.data.maindb.plan.Schedule
import java.util.Date


@Entity(
    tableName = "raw_schedule",
)
data class RawSchedule(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    override val id: Int = 0,
    @ColumnInfo(name = "title")
    override val title: String,
    @ColumnInfo(name = "start_time")
    val startTime: Date,
    @ColumnInfo(name = "end_time")
    val endTime: Date,
    @ColumnInfo(name = "memo", defaultValue = "")
    override val memo: String = "",
    @ColumnInfo(name = "repeat_group_id", defaultValue = "NULL")
    override val repeatGroupId: Int? = null,
    @ColumnInfo(name = "category_id", defaultValue = "NULL")
    override val categoryId: Int? = null,
    @ColumnInfo(name = "priority", defaultValue = "0") // 0 = raw-sql without priority specified
    override val priority: Int = 3,
    @ColumnInfo(name = "show_in_monthly_view", defaultValue = "1")
    override val showInMonthlyView: Boolean = true,
    @ColumnInfo(name = "is_overridden", defaultValue = "0")
    override val isOverridden: Boolean = false
) : RawPlan {
    fun toSchedule(): Schedule = Schedule(
        id = id, // Should use same id for successful update
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

fun Schedule.toRawSchedule() = RawSchedule(
    id = id, // Should use same id for successful update
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