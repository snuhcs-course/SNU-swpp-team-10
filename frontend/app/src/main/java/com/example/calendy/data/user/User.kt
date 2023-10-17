package com.example.calendy.data.user
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "user")
data class User(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        var id: Int = 0,
        @ColumnInfo(name = "email")
        var email: String,
        @ColumnInfo(name = "name")
        var name: String,
        @ColumnInfo(name = "Password_hash")
        var passwordHash: String,
        @ColumnInfo(name = "created_at")
        var createdAt: Date
)