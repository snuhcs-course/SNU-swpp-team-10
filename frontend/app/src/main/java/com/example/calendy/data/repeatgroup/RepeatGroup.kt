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
    @ColumnInfo(name = "day")
    @SerializedName("day")
    var day: Boolean,
    @ColumnInfo(name = "week")
    @SerializedName("week")
    var week: Boolean,
    @ColumnInfo(name = "month")
    @SerializedName("month")
    var month: Boolean,
    @ColumnInfo(name = "year")
    @SerializedName("year")
    var year: Boolean,
    @ColumnInfo(name = "repeat_interval")
    @SerializedName("repeat_interval")
    var repeatInterval: Int,
    @ColumnInfo(name = "repeat_rule")
    @SerializedName("repeat_rule")
    var repeatRule: String?,
    @ColumnInfo(name = "end_date")
    @SerializedName("end_date")
    var endDate: Date?
)