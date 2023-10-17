package com.example.calendy.data.category

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.calendy.data.user.User

@Entity(tableName = "category",
        foreignKeys =
        [ForeignKey(entity = User::class,
                parentColumns = ["id"],
                childColumns = ["userId"],
                onDelete = ForeignKey.CASCADE)])
data class Category(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        var id: Int = 0,
        @PrimaryKey
        @ColumnInfo(name = "user_id")
        var userId: Int,
        @ColumnInfo(name = "title")
        var title: String,
        @ColumnInfo(name = "default_priority")
        var defaultPriority: Int
)