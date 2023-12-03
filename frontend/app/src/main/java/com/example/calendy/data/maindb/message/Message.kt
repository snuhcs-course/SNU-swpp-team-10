package com.example.calendy.data.maindb.message

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "message")
data class Message(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "sent_time")
    val sentTime: Date,
    @ColumnInfo(name = "message_from_manager")
    val messageFromManager: Boolean = false,
    @ColumnInfo(name = "content")
    val content: String = "",
    @ColumnInfo(name = "has_log_plan")
    val hasRevision: Boolean = false, // "자세히 보기"를 제공해야 한다.
    @ColumnInfo(name = "user_message_id")
    val userMessageId: Int = id, // Manager의 Response는 User Request에 대한 그룹을 형성한다.
)
