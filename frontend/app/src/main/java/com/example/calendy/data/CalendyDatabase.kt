package com.example.calendy.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Schedule::class], version = 1, exportSchema = false)
@TypeConverters(RoomConverters::class)
abstract class CalendyDatabase: RoomDatabase() {
    abstract fun scheduleDao(): ScheduleDao
    abstract fun todoDao(): TodoDao

    companion object {
        @Volatile
        private var Instance: CalendyDatabase? = null
        fun getDatabase(context: Context): CalendyDatabase {
            return Instance ?: synchronized(this) {
                // name: The name of the database file.
                Room.databaseBuilder(context = context, klass = CalendyDatabase::class.java, name = "calendy_database")
                        .fallbackToDestructiveMigration()
                        .build()
                        .also { Instance = it }
            }
        }
    }
}