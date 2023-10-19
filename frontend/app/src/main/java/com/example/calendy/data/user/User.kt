package com.example.calendy.data.user
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.Date

@Entity(tableName = "user")
data class User(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        @SerializedName("id")
        var id: Int = 0,
        @ColumnInfo(name = "email")
        @SerializedName("email")
        var email: String,
        @ColumnInfo(name = "name")
        @SerializedName("name")
        var name: String,
        @ColumnInfo(name = "Password_hash")
        @SerializedName("Password_hash")
        var passwordHash: String,
        @ColumnInfo(name = "created_at")
        @SerializedName("created_at")
        var createdAt: Date
)