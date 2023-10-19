package com.example.calendy.data.schedule
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.calendy.data.category.Category
import com.example.calendy.data.user.User
import com.google.gson.annotations.SerializedName
import java.util.Date


@Entity(tableName = "schedule",
        foreignKeys =
        [ForeignKey(entity = User::class,
                    parentColumns = ["id"],
                    childColumns = ["user_id"],
                    onDelete = ForeignKey.CASCADE),
            ForeignKey(entity = Category::class,
                    parentColumns = ["id"],
                    childColumns = ["category_id"],
                    onDelete = ForeignKey.SET_DEFAULT ) ])
data class Schedule(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        @SerializedName("id")
        var id: Int = 0,
        @ColumnInfo(name = "user_id")
        @SerializedName("user_id")
        var userId: Int,
        @ColumnInfo(name = "title")
        @SerializedName("title")
        var title: String,
        @ColumnInfo(name = "start_time")
        @SerializedName("start_time")
        var startTime: Date,
        @ColumnInfo(name = "end_time")
        @SerializedName("end_time")
        var endTime: Date,
        @ColumnInfo(name = "memo")
        @SerializedName("memo")
        var memo : String,
        @ColumnInfo(name = "repeat_group_id")
        @SerializedName("repeat_group_id")
        var repeatGroupId: Int,
        @ColumnInfo(name = "category_id")
        @SerializedName("category_id")
        var categoryId: Int = 0,
        @ColumnInfo(name = "priority")
        @SerializedName("priority")
        var priority: Int,
        @ColumnInfo(name = "show_in_monthly_view")
        @SerializedName("show_in_monthly_view")
        var showInMonthlyView: Boolean,
        @ColumnInfo(name = "is_overridden")
        @SerializedName("is_overridden")
        var isOverridden: Boolean
)
