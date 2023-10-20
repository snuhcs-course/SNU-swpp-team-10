package com.example.calendy.data.category

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "category")
data class Category(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    @SerializedName("id")
    var id: Int = 0,
    @ColumnInfo(name = "title")
    @SerializedName("title")
    var title: String,
    @ColumnInfo(name = "default_priority")
    @SerializedName("default_priority")
    var defaultPriority: Int
)