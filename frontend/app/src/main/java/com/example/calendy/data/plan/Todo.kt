package com.example.calendy.data.plan

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.calendy.data.category.Category
import com.example.calendy.data.repeatgroup.RepeatGroup
import com.google.gson.annotations.SerializedName
import java.util.Date

// table name tod0(=t0do) in 'calendy_database.db'
@Entity(
    tableName = "todo", foreignKeys = [
//        ForeignKey(
//        entity = Category::class,
//        parentColumns = ["id"],
//        childColumns = ["category_id"],
//        onDelete = ForeignKey.SET_NULL
//    ),
        ForeignKey(
        entity = RepeatGroup::class,
        parentColumns = ["id"],
        childColumns = ["repeat_group_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Todo(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    @SerializedName("id")
    override val id: Int = 0,
    @ColumnInfo(name = "title")
    @SerializedName("title")
    override val title: String,
    @ColumnInfo(name = "due_time")
    @SerializedName("due_time")
    val dueTime: Date,
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
    override val memo: String = "",
    @ColumnInfo(name = "repeat_group_id", defaultValue = "NULL")
    @SerializedName("repeat_group_id")
    override val repeatGroupId: Int? = null,
    @ColumnInfo(name = "category_id", defaultValue = "NULL")
    @SerializedName("category_id")
    override val categoryId: Int? = null,
    @ColumnInfo(name = "priority", defaultValue = "NULL")
    @SerializedName("priority")
    override val priority: Int? = null,
    @ColumnInfo(name = "show_in_monthly_view", defaultValue = "0")
    @SerializedName("show_in_monthly_view")
    override val showInMonthlyView: Boolean = false,
    @ColumnInfo(name = "is_overridden", defaultValue = "0")
    @SerializedName("is_overridden")
    override val isOverridden: Boolean = false
) : Plan
