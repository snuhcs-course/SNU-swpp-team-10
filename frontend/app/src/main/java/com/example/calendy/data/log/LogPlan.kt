package com.example.calendy.data.log

import androidx.room.ColumnInfo
import com.google.gson.annotations.SerializedName

interface LogPlan {
    val id: Int
    val messageId: Int
    val logType: String // "INSERT", "UPDATE", "DELETE"
    val planId: Int? // null when logType = "INSERT"
}