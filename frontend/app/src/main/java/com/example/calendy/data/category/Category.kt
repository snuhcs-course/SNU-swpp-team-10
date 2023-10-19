package com.example.calendy.data.category

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.calendy.data.user.User
import com.google.gson.annotations.SerializedName

@Entity(tableName = "category",
        foreignKeys =
        [ForeignKey(entity = User::class,
                parentColumns = ["id"],
                childColumns = ["user_id"],
                onDelete = ForeignKey.CASCADE)])
data class Category(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        @SerializedName("id")
        var id: Int = 1,
        @ColumnInfo(name = "user_id")
        @SerializedName("user_id")
        var userId: Int,
        @ColumnInfo(name = "title")
        @SerializedName("title")
        var title: String,
        @ColumnInfo(name = "default_priority")
        @SerializedName("default_priority")
        var defaultPriority: Int
)