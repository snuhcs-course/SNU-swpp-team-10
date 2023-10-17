package com.example.calendy.data.message

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.calendy.data.user.User
import java.util.Date

@Entity(tableName = "message",
        foreignKeys =
        [ForeignKey(entity = User::class,
                parentColumns = ["id"],
                childColumns = ["userId"],
                onDelete = ForeignKey.CASCADE)])
data class Message(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        var id: Int = 0,
        @PrimaryKey
        @ColumnInfo(name = "user_id")
        var userId: Int,
        @ColumnInfo(name = "sent_time")
        var sentTime: Date,
        @ColumnInfo(name = "message_from_manager")
        var messageFromManager: Boolean,
        @ColumnInfo(name = "content")
        var content: String
)
