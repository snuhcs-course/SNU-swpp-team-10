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
    val id: Int = 0,
    @ColumnInfo(name = "day", defaultValue = "0")
    @SerializedName("day")
    var day: Boolean = false,
    @ColumnInfo(name = "week", defaultValue = "0")
    @SerializedName("week")
    var week: Boolean = false,
    @ColumnInfo(name = "month", defaultValue = "0")
    @SerializedName("month")
    var month: Boolean = false,
    @ColumnInfo(name = "year", defaultValue = "0")
    @SerializedName("year")
    var year: Boolean = false,
    @ColumnInfo(name = "repeat_interval", defaultValue = "0")
    @SerializedName("repeat_interval")
    var repeatInterval: Int = 0,
    @ColumnInfo(name = "repeat_rule", defaultValue = "NULL")
    @SerializedName("repeat_rule")
    var repeatRule: String? = null,
    @ColumnInfo(name = "end_date")
    @SerializedName("end_date")
    var endDate: Date?
)