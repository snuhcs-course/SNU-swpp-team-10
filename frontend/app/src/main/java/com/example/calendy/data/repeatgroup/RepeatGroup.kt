package com.example.calendy.data.repeatgroup
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.Date

@Entity(tableName = "repeat_group")
data class RepeatGroup(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    @SerializedName("id")
    val id: Int,
    @ColumnInfo(name = "day")
    @SerializedName("day")
    val day: Boolean,
    @ColumnInfo(name = "week")
    @SerializedName("week")
    val week: Boolean,
    @ColumnInfo(name = "month")
    @SerializedName("month")
    val month: Boolean,
    @ColumnInfo(name = "year")
    @SerializedName("year")
    val year: Boolean,
    @ColumnInfo(name = "repeat_interval")
    @SerializedName("repeat_interval")
    val repeatInterval: Int,
    @ColumnInfo(name = "repeat_rule")
    @SerializedName("repeat_rule")
    val repeatRule: String?,
    @ColumnInfo(name = "end_date")
    @SerializedName("end_date")
    val endDate: Date?
)