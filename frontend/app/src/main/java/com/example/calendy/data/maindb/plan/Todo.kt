package com.example.calendy.data.maindb.plan

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.calendy.data.maindb.category.Category
import com.example.calendy.data.maindb.plan.Plan.Companion.PRIORITY_DEFAULT
import com.example.calendy.data.maindb.repeatgroup.RepeatGroup
import java.util.Date
import java.util.Locale

// table name tod0(=t0do) in 'calendy_database.db'
@Entity(
    tableName = "todo", foreignKeys = [ForeignKey(
        entity = Category::class,
        parentColumns = ["id"],
        childColumns = ["category_id"],
        onDelete = ForeignKey.SET_NULL
    ), ForeignKey(
        entity = RepeatGroup::class,
        parentColumns = ["id"],
        childColumns = ["repeat_group_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Todo(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    override val id: Int = 0,
    @ColumnInfo(name = "title")
    override val title: String,
    @ColumnInfo(name = "due_time")
    val dueTime: Date,
    @ColumnInfo(name = "complete")
    val complete: Boolean = false,
    @ColumnInfo(name = "memo")
    override val memo: String = "",
    @ColumnInfo(name = "category_id", index = true)
    override val categoryId: Int? = null,
    @ColumnInfo(name = "priority")
    override val priority: Int = PRIORITY_DEFAULT,
    @ColumnInfo(name = "show_in_monthly_view")
    override val showInMonthlyView: Boolean = true,
    @ColumnInfo(name = "repeat_group_id", index = true)
    override val repeatGroupId: Int? = null,
    @ColumnInfo(name = "is_overridden")
    override val isOverridden: Boolean = false
) : Plan {
    override fun typedSummary(): Pair<String, String> {
        return ("Todo" to "dueTime=$dueTime, complete=$complete")
    }
}
