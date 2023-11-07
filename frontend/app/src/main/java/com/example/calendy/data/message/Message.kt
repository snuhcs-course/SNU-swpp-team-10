package com.example.calendy.data.message

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.Date

@Entity(tableName = "message")
data class Message(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    @SerializedName("id")
    val id: Int = 0,
    @ColumnInfo(name = "sent_time")
    @SerializedName("sent_time")
    val sentTime: Date,
    @ColumnInfo(name = "message_from_manager", defaultValue = "0")
    @SerializedName("message_from_manager")
    val messageFromManager: Boolean = false,
    @ColumnInfo(name = "content", defaultValue = "")
    @SerializedName("content")
    val content: String = "",
    @ColumnInfo(name = "has_log_plan", defaultValue = "0")
    @SerializedName("has_log_plan")
    val hasLogPlan: Boolean = false, // 연결되는 LogSchedule 혹은 LogTodo가 존재한다.
)
