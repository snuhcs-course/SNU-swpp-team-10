package com.example.calendy.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Schedule::class], version = 1, exportSchema = false)
@TypeConverters(RoomConverters::class)
abstract class ScheduleDatabase : RoomDatabase() {
    abstract fun scheduleDao(): ScheduleDao

    companion object {
        @Volatile
        private var Instance: ScheduleDatabase? = null
        fun getDatabase(context: Context): ScheduleDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context = context, klass = ScheduleDatabase::class.java, name = "schedule_database")
                        .fallbackToDestructiveMigration()
                        .build()
                        .also { Instance = it }
            }
        }
    }
}