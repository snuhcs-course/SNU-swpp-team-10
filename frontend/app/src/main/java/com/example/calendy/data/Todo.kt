package com.example.calendy.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// TODO: Name의 역할 알아보기
@Entity(tableName = "todo")
data class Todo(
        @PrimaryKey
        @ColumnInfo(name = "uuid")
        val uuid: String,
        @ColumnInfo(name = "title")
        val title: String,
        @ColumnInfo(name = "due_year")
        val dueYear: Int,
        @ColumnInfo(name = "due_month")
        val dueMonth: Int,
        @ColumnInfo(name = "due_day")
        val dueDay: Int,
        @ColumnInfo(name = "due_hour")
        val dueHour: Int,
        @ColumnInfo(name = "due_minute")
        val dueMinute: Int,
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
