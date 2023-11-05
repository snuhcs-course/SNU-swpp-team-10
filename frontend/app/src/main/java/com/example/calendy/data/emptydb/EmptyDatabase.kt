package com.example.calendy.data.emptydb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.calendy.data.RoomConverters
import com.example.calendy.data.category.Category
import com.example.calendy.data.category.CategoryDao
import com.example.calendy.data.message.Message
import com.example.calendy.data.message.MessageDao
import com.example.calendy.data.plan.Plan
import com.example.calendy.data.plan.Schedule
import com.example.calendy.data.plan.Todo
import com.example.calendy.data.plan.schedule.ScheduleDao
import com.example.calendy.data.plan.todo.TodoDao
import com.example.calendy.data.repeatgroup.RepeatGroup
import com.example.calendy.data.repeatgroup.RepeatGroupDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.Date

@Database(
    entities = [Schedule::class, Todo::class, Category::class, Message::class, RepeatGroup::class],
    version = 7,
    exportSchema = false
)
@TypeConverters(RoomConverters::class)
abstract class EmptyDatabase : RoomDatabase() {
    abstract fun scheduleDao(): ScheduleDao
    abstract fun todoDao(): TodoDao
    abstract fun categoryDao(): CategoryDao
    abstract fun messageDao(): MessageDao
    abstract fun repeatGroupDao(): RepeatGroupDao

    private val tableList = listOf("schedule", "todo", "category", "message", "repeat_group")
    fun deleteAll() {
        for (table in tableList) {
            this.openHelper.writableDatabase.execSQL("DELETE from $table")
        }
    }

    fun getAllPlansStream(): Flow<List<Plan>> {
        val schedulesStream = scheduleDao().getAllSchedule()
        val todosStream = todoDao().getAllTodo()
        return combine(schedulesStream, todosStream) { scheduleList, todoList ->
            val result = scheduleList + todoList
            result
        }
    }

    companion object {
        @Volatile
        private var Instance: EmptyDatabase? = null
        fun getDatabase(context: Context): EmptyDatabase {
            return Instance ?: synchronized(this) {
                // name: The name of the database file.
                Room.inMemoryDatabaseBuilder(
                    context = context,
                    klass = EmptyDatabase::class.java,
                ).fallbackToDestructiveMigration().build().also { Instance = it }
            }
        }
    }
}