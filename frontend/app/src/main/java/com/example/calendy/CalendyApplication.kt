package com.example.calendy

import android.app.Application
import android.content.Context
import com.example.calendy.data.CalendyDatabase
import com.example.calendy.data.IScheduleRepository
import com.example.calendy.data.ITodoRepository
import com.example.calendy.data.ScheduleLocalDataSource
import com.example.calendy.data.ScheduleRepository
import com.example.calendy.data.TodoLocalDataSource
import com.example.calendy.data.TodoRepository

class CalendyApplication : Application() {
    lateinit var container: IAppContainer
    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}

interface IAppContainer {
    val scheduleRepository : IScheduleRepository
    val todoRepository : ITodoRepository
}
class AppContainer(private val context: Context) : IAppContainer {
    override val scheduleRepository: IScheduleRepository by lazy {
        ScheduleRepository(ScheduleLocalDataSource(CalendyDatabase.getDatabase(context).scheduleDao()))
    }
    override val todoRepository: ITodoRepository by lazy {
        TodoRepository(TodoLocalDataSource(CalendyDatabase.getDatabase(context).todoDao()))
    }
}