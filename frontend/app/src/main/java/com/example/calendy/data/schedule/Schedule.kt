package com.example.calendy.data.schedule
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.calendy.data.category.Category
import com.example.calendy.data.user.User
import java.util.Date


@Entity(tableName = "schedule",
        foreignKeys =
        [ForeignKey(entity = User::class,
                    parentColumns = ["id"],
                    childColumns = ["userId"],
                    onDelete = ForeignKey.CASCADE),
            ForeignKey(entity = Category::class,
                    parentColumns = ["id"],
                    childColumns = ["categoryID"])])
data class Schedule(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        var id: Int = 0,
        @PrimaryKey
        @ColumnInfo(name = "user_id")
        var userId: Int,
        @ColumnInfo(name = "title")
        var title: String,
        @ColumnInfo(name = "start_time")
        var startTime: Date,
        @ColumnInfo(name = "end_time")
        var endTime: Date,
        @ColumnInfo(name = "memo")
        var memo : String,
        @ColumnInfo(name = "repeat_group_id")
        var repeatGroupId: Int,
        @ColumnInfo(name = "category_id")
        var categoryId: Int,
        @ColumnInfo(name = "priority")
        var priority: Int,
        @ColumnInfo(name = "show_in_monthly_view")
        var showInMonthlyView: Boolean,
        @ColumnInfo(name = "is_overridden")
        var isOverridden: Boolean
)
