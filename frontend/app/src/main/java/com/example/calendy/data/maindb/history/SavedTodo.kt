package com.example.calendy.data.maindb.history

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.calendy.data.maindb.plan.Todo
import java.util.Date

@Entity(
    tableName = "saved_todo"
)
data class SavedTodo(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    override val id: Int = 0,
    @ColumnInfo(name = "title")
    override val title: String,
    @ColumnInfo(name = "due_time")
    val dueTime: Date,
    @ColumnInfo(name = "complete")
    val complete: Boolean,
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
    fun toTodo(): Todo = Todo(
        id = 0,
        title = title,
        dueTime = dueTime,
        complete = complete,
        memo = memo,
        repeatGroupId = repeatGroupId,
        categoryId = categoryId,
        priority = priority,
        showInMonthlyView = showInMonthlyView,
        isOverridden = isOverridden
    )
}

fun Todo.toSavedTodo(): SavedTodo = SavedTodo(
    id = 0,
    title = title,
    dueTime = dueTime,
    complete = complete,
    memo = memo,
    repeatGroupId = repeatGroupId,
    categoryId = categoryId,
    priority = priority,
    showInMonthlyView = showInMonthlyView,
    isOverridden = isOverridden,
)