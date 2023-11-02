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
    @ColumnInfo(name = "day", defaultValue = "0")
    @SerializedName("day")
    val day: Boolean = false,
    @ColumnInfo(name = "week", defaultValue = "0")
    @SerializedName("week")
    val week: Boolean = false,
    @ColumnInfo(name = "month", defaultValue = "0")
    @SerializedName("month")
    val month: Boolean = false,
    @ColumnInfo(name = "year", defaultValue = "0")
    @SerializedName("year")
    val year: Boolean = false,
    @ColumnInfo(name = "repeat_interval", defaultValue = "0")
    @SerializedName("repeat_interval")
    val repeatInterval: Int = 0,
    @ColumnInfo(name = "repeat_rule", defaultValue = "NULL")
    @SerializedName("repeat_rule")
    val repeatRule: String? = null,
    @ColumnInfo(name = "end_date")
    @SerializedName("end_date")
    val endDate: Date?
)