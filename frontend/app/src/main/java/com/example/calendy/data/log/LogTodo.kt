package com.example.calendy.data.log

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.calendy.data.category.Category
import com.example.calendy.data.message.Message
import com.example.calendy.data.plan.Todo
import com.example.calendy.data.repeatgroup.RepeatGroup
import com.google.gson.annotations.SerializedName
import java.util.Date

@Entity(
    tableName = "log_todo", foreignKeys = [ForeignKey(
        entity = Category::class,
        parentColumns = ["id"],
        childColumns = ["category_id"],
        onDelete = ForeignKey.SET_DEFAULT
    ), ForeignKey(
        entity = RepeatGroup::class,
        parentColumns = ["id"],
        childColumns = ["repeat_group_id"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = Todo::class,
        parentColumns = ["id"],
        childColumns = ["plan_id"],
        onDelete = ForeignKey.SET_DEFAULT
    ), ForeignKey(
        entity = Message::class,
        parentColumns = ["id"],
        childColumns = ["message_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class LogTodo(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    @SerializedName("id")
    override val id: Int = 0,
    @ColumnInfo(name = "message_id")
    @SerializedName("message_id")
    override val messageId: Int, // connected message
    @ColumnInfo(name = "log_type")
    @SerializedName("log_type")
    override val logType: String, // "INSERT", "UPDATE", "DELETE"
    @ColumnInfo(name = "plan_id")
    @SerializedName("plan_id")
    override val planId: Int?, // null when logType = "INSERT"
    @ColumnInfo(name = "title")
    @SerializedName("title")
    val title: String? = null,
    @ColumnInfo(name = "due_time")
    @SerializedName("due_time")
    val dueTime: Date? = null,
    @ColumnInfo(name = "yearly", defaultValue = "0")
    @SerializedName("yearly")
    val yearly: Boolean = false,
    @ColumnInfo(name = "monthly", defaultValue = "0")
    @SerializedName("monthly")
    val monthly: Boolean = false,
    @ColumnInfo(name = "daily", defaultValue = "0")
    @SerializedName("daily")
    val daily: Boolean = false,
    @ColumnInfo(name = "complete", defaultValue = "0")
    @SerializedName("complete")
    val complete: Boolean = false,
    @ColumnInfo(name = "memo", defaultValue = "")
    @SerializedName("memo")
    val memo: String = "",
    @ColumnInfo(name = "repeat_group_id", defaultValue = "NULL")
    @SerializedName("repeat_group_id")
    val repeatGroupId: Int? = null,
    @ColumnInfo(name = "category_id", defaultValue = "NULL")
    @SerializedName("category_id")
    val categoryId: Int? = null,
    @ColumnInfo(name = "priority", defaultValue = "3")
    @SerializedName("priority")
    val priority: Int = 3,
    @ColumnInfo(name = "show_in_monthly_view", defaultValue = "0")
    @SerializedName("show_in_monthly_view")
    val showInMonthlyView: Boolean = false,
    @ColumnInfo(name = "is_overridden", defaultValue = "0")
    @SerializedName("is_overridden")
    val isOverridden: Boolean = false
): LogPlan