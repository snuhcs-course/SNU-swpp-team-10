package com.example.calendy.data.rawsqldb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.calendy.data.RoomConverters
import com.example.calendy.data.maindb.plan.Plan
import com.example.calendy.data.maindb.plan.Schedule
import com.example.calendy.data.maindb.plan.Todo

@Database(
    entities = [RawSqlSchedule::class, RawSqlTodo::class], version = 2, exportSchema = false
)
@TypeConverters(RoomConverters::class)
abstract class RawSqlDatabase : RoomDatabase() {
    abstract fun rawSqlScheduleDao(): RawSqlScheduleDao
    abstract fun rawSqlTodoDao(): RawSqlTodoDao

    // get RawSqlPlan then convert to Plan
    fun getAllPlans(): List<Plan> =
        rawSqlScheduleDao().getAllRawSqlSchedules().map { it.toSchedule() } +
                rawSqlTodoDao().getAllRawSqlTodos().map { it.toTodo() }

    suspend fun insertFromPlan(plan: Plan): Long = when (plan) {
        is Schedule -> rawSqlScheduleDao().insert(plan.toRawSqlSchedule())
        is Todo     -> rawSqlTodoDao().insert(plan.toRawSqlTodo())
    }

    fun deleteAll() {
        rawSqlScheduleDao().deleteAllRawSqlSchedules()
        rawSqlTodoDao().deleteAllRawSqlTodos()
    }

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
        private var Instance: RawSqlDatabase? = null
        fun getDatabase(context: Context): RawSqlDatabase {
            return Instance ?: synchronized(this) {
                // In Memory Database
                Room.inMemoryDatabaseBuilder(
                    context = context,
                    klass = RawSqlDatabase::class.java,
                ).fallbackToDestructiveMigration().build().also { Instance = it }
            }
        }
    }
}