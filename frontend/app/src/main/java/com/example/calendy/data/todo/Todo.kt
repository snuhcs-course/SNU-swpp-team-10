package com.example.calendy.data.todo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.calendy.data.category.Category
import com.example.calendy.data.user.User
import java.util.Date

// table name `todo` in 'calendy_database.db'
@Entity(tableName = "todo",
        foreignKeys =
        [ForeignKey(entity = User::class,
                    parentColumns = ["id"],
                    childColumns = ["userId"],
                    onDelete = ForeignKey.CASCADE),
            ForeignKey(entity = Category::class,
                    parentColumns = ["id"],
                    childColumns = ["categoryID"])])
data class Todo(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        val id: Int = 0,
        @PrimaryKey
        @ColumnInfo(name = "user_id")
        val userId: Int = 0,
        @ColumnInfo(name = "title")
        val title: String,
        @ColumnInfo(name = "due_time")
        val dueTime: Date,
        @ColumnInfo(name = "yearly")
        val yearly: Boolean,
        @ColumnInfo(name = "monthly")
        val monthly: Boolean,
        @ColumnInfo(name = "daily")
        val daily: Boolean,
        @ColumnInfo(name = "complete")
        val complete: Boolean,
        @ColumnInfo(name = "memo")
        val memo: String,
        @ColumnInfo(name = "repeat_group_id")
        val repeatGroupId: Int,
        @ColumnInfo(name = "category_id")
        val categoryId: Int,
        @ColumnInfo(name = "priority")
        val priority: Int,
        @ColumnInfo(name = "show_in_monthly_view")
        var showInMonthlyView: Boolean,
        @ColumnInfo(name = "is_overridden")
        var isOverridden: Boolean
)
