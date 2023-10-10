package com.example.calendy.data
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date


@Entity(tableName = "schedule")
data class Schedule(
        @PrimaryKey
        @ColumnInfo(name = "uuid")
        var uuid: String,
        @ColumnInfo(name = "title")
        var title: String,
        @ColumnInfo(name = "start_time")
        var startTime: Date,
        @ColumnInfo(name = "end_time")
        var endTime: Date,
        @ColumnInfo(name = "repeat_group_id")
        var repeatGroupId: Int,
        @ColumnInfo(name = "category_id")
        var categoryId: Int,
        @ColumnInfo(name = "priority")
        var priority: Int
)
