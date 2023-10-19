package com.example.calendy.data.message

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.Date

@Entity(tableName = "message")
data class Message(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        @SerializedName("id")
        var id: Int = 0,
        @ColumnInfo(name = "sent_time")
        @SerializedName("sent_time")
        var sentTime: Date,
        @ColumnInfo(name = "message_from_manager")
        @SerializedName("message_from_manager")
        var messageFromManager: Boolean,
        @ColumnInfo(name = "content")
        @SerializedName("content")
        var content: String
)
