package com.example.calendy.data.log

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.calendy.data.category.Category
import com.example.calendy.data.message.Message
import com.example.calendy.data.plan.Schedule
import com.example.calendy.data.repeatgroup.RepeatGroup
import com.google.gson.annotations.SerializedName
import java.util.Date

@Entity(
    tableName = "log_schedule", foreignKeys = [ForeignKey(
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
        entity = Schedule::class,
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
data class LogSchedule(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    @SerializedName("id")
    val id: Int = 0,
    @ColumnInfo(name = "message_id")
    @SerializedName("message_id")
    val messageId: Int, // connected message
    @ColumnInfo(name = "log_type")
    @SerializedName("log_type")
    val logType: String, // "INSERT", "UPDATE", "DELETE"
    @ColumnInfo(name = "plan_id")
    @SerializedName("plan_id")
    val planId: Int?, // null when logType = "INSERT"
    @ColumnInfo(name = "title")
    @SerializedName("title")
    val title: String? = null,
    @ColumnInfo(name = "start_time")
    @SerializedName("start_time")
    val startTime: Date? = null,
    @ColumnInfo(name = "end_time")
    @SerializedName("end_time")
    val endTime: Date? = null,
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
)
