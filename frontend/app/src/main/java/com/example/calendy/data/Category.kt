package com.example.calendy.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "category")
data class Category (
    @PrimaryKey
    @ColumnInfo(name = "ID")
    var id: Int,
    @ColumnInfo(name = "user_id")
    var userId: Int,
    @ColumnInfo(name = "title")
    var title: String,
    @ColumnInfo(name = "default_priority")
    var defaultPriority: Int
)