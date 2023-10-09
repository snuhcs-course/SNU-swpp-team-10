package com.example.calendy.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Todo::class], version = 1, exportSchema = false)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao

    companion object {
        @Volatile
        private var Instance: TodoDatabase? = null
        fun getDatabase(context: Context): TodoDatabase {
            return Instance ?: synchronized(this) {
                // TODO: name의 역할 알아보기
                Room.databaseBuilder(context = context, klass = TodoDatabase::class.java, name = "todo_database")
                        .fallbackToDestructiveMigration()
                        .build()
                        .also { Instance = it }
            }
        }
    }
}