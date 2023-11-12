package com.example.calendy.data.maindb.repeatgroup

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "repeat_group")
data class RepeatGroup(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "day")
    var day: Boolean = false,
    @ColumnInfo(name = "week")
    var week: Boolean = false,
    @ColumnInfo(name = "month")
    var month: Boolean = false,
    @ColumnInfo(name = "year")
    var year: Boolean = false,
    @ColumnInfo(name = "repeat_interval")
    var repeatInterval: Int = 0,
    @ColumnInfo(name = "repeat_rule")
    var repeatRule: String? = null,
    @ColumnInfo(name = "end_date")
    var endDate: Date?
)