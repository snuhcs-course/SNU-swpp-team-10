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
    val content: String = ""
)
