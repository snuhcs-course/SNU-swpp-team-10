package com.example.calendy.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.calendy.data.category.Category
import com.example.calendy.data.category.CategoryDao
import com.example.calendy.data.log.LogSchedule
import com.example.calendy.data.log.LogScheduleDao
import com.example.calendy.data.log.LogTodo
import com.example.calendy.data.log.LogTodoDao
import com.example.calendy.data.message.Message
import com.example.calendy.data.message.MessageDao
import com.example.calendy.data.plan.Schedule
import com.example.calendy.data.plan.Todo
import com.example.calendy.data.plan.schedule.ScheduleDao
import com.example.calendy.data.plan.todo.TodoDao
import com.example.calendy.data.repeatgroup.RepeatGroup
import com.example.calendy.data.repeatgroup.RepeatGroupDao

@Database(
    entities = [Schedule::class, Todo::class, Category::class, Message::class, RepeatGroup::class, LogSchedule::class, LogTodo::class],
    version = 9,
    exportSchema = false
)
@TypeConverters(RoomConverters::class)
abstract class CalendyDatabase : RoomDatabase() {
    abstract fun scheduleDao(): ScheduleDao
    abstract fun todoDao(): TodoDao
    abstract fun categoryDao(): CategoryDao
    abstract fun repeatGroupDao(): RepeatGroupDao
    abstract fun messageDao(): MessageDao
    abstract fun logScheduleDao(): LogScheduleDao
    abstract fun logTodoDao(): LogTodoDao

    companion object {
        @Volatile
        private var Instance: CalendyDatabase? = null
        fun getDatabase(context: Context): CalendyDatabase {
            return Instance ?: synchronized(this) {
                // name: The name of the database file.
                Room.databaseBuilder(
                    context = context,
                    klass = CalendyDatabase::class.java,
                    name = "calendy_database"
                ).fallbackToDestructiveMigration().build().also { Instance = it }
            }
        }
    }
}