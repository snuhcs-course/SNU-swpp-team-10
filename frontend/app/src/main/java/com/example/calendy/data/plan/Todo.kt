package com.example.calendy.data.plan

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.calendy.data.category.Category
import com.google.gson.annotations.SerializedName
import java.util.Date

// table name `todo` in 'calendy_database.db'
@Entity(
    tableName = "todo", foreignKeys = [ForeignKey(
        entity = Category::class,
        parentColumns = ["id"],
        childColumns = ["category_id"],
        onDelete = ForeignKey.SET_NULL
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
    @ColumnInfo(name = "yearly")
    @SerializedName("yearly")
    val yearly: Boolean,
    @ColumnInfo(name = "monthly")
    @SerializedName("monthly")
    val monthly: Boolean,
    @ColumnInfo(name = "daily")
    @SerializedName("daily")
    val daily: Boolean,
    @ColumnInfo(name = "complete")
    @SerializedName("complete")
    val complete: Boolean,
    @ColumnInfo(name = "memo")
    @SerializedName("memo")
    override val memo: String,
    @ColumnInfo(name = "repeat_group_id")
    @SerializedName("repeat_group_id")
    override val repeatGroupId: Int? = null,
    @ColumnInfo(name = "category_id")
    @SerializedName("category_id")
    override val categoryId: Int? = null,
    @ColumnInfo(name = "priority")
    @SerializedName("priority")
    override val priority: Int,
    @ColumnInfo(name = "show_in_monthly_view")
    @SerializedName("show_in_monthly_view")
    override val showInMonthlyView: Boolean,
    @ColumnInfo(name = "is_overridden")
    @SerializedName("is_overridden")
    override val isOverridden: Boolean
) : Plan
