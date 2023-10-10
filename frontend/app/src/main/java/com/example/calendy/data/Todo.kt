package com.example.calendy.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

// table name `todo` in 'calendy_database.db'
@Entity(tableName = "todo")
data class Todo(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        val id: Int = 0,
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
)
