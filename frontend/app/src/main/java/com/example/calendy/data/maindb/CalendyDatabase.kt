package com.example.calendy.data.maindb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.calendy.data.RoomConverters
import com.example.calendy.data.maindb.category.Category
import com.example.calendy.data.maindb.category.CategoryDao
import com.example.calendy.data.maindb.history.ManagerHistory
import com.example.calendy.data.maindb.history.ManagerHistoryDao
import com.example.calendy.data.maindb.history.SavedSchedule
import com.example.calendy.data.maindb.history.SavedScheduleDao
import com.example.calendy.data.maindb.history.SavedTodo
import com.example.calendy.data.maindb.history.SavedTodoDao
import com.example.calendy.data.maindb.message.Message
import com.example.calendy.data.maindb.message.MessageDao
import com.example.calendy.data.maindb.plan.Schedule
import com.example.calendy.data.maindb.plan.Todo
import com.example.calendy.data.maindb.plan.schedule.ScheduleDao
import com.example.calendy.data.maindb.plan.todo.TodoDao
import com.example.calendy.data.maindb.rawplan.RawSchedule
import com.example.calendy.data.maindb.rawplan.RawScheduleDao
import com.example.calendy.data.maindb.rawplan.RawTodo
import com.example.calendy.data.maindb.rawplan.RawTodoDao
import com.example.calendy.data.maindb.repeatgroup.RepeatGroup
import com.example.calendy.data.maindb.repeatgroup.RepeatGroupDao

@Database(
    entities = [Schedule::class, Todo::class, Category::class, Message::class, RepeatGroup::class, ManagerHistory::class, SavedSchedule::class, SavedTodo::class, RawSchedule::class, RawTodo::class],
    version = 12,
    exportSchema = false
)
@TypeConverters(RoomConverters::class)
abstract class CalendyDatabase : RoomDatabase() {
    abstract fun scheduleDao(): ScheduleDao
    abstract fun todoDao(): TodoDao
    abstract fun categoryDao(): CategoryDao
    abstract fun repeatGroupDao(): RepeatGroupDao
    abstract fun messageDao(): MessageDao
    abstract fun managerHistoryDao(): ManagerHistoryDao
    abstract fun savedScheduleDao(): SavedScheduleDao
    abstract fun savedTodoDao(): SavedTodoDao
    abstract fun rawScheduleDao(): RawScheduleDao
    abstract fun rawTodoDao(): RawTodoDao

    private var writableDatabaseInstance: SupportSQLiteDatabase? = null
    private fun getWritableDatabase(): SupportSQLiteDatabase {
        return writableDatabaseInstance
            ?: this.openHelper.writableDatabase.also { writableDatabaseInstance = it }
    }

    fun execSql(sqlQuery: String) {
        getWritableDatabase().execSQL(sqlQuery)
    }

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