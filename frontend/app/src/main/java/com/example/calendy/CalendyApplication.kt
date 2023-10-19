package com.example.calendy

import android.app.Application
import android.content.Context
import com.example.calendy.data.CalendyDatabase
import com.example.calendy.data.category.CategoryLocalDataSource
import com.example.calendy.data.category.CategoryRepository
import com.example.calendy.data.category.ICategoryRepository
import com.example.calendy.data.message.IMessageRepository
import com.example.calendy.data.message.MessageLocalDataSource
import com.example.calendy.data.message.MessageRepository
import com.example.calendy.data.schedule.IScheduleRepository
import com.example.calendy.data.todo.ITodoRepository
import com.example.calendy.data.schedule.ScheduleLocalDataSource
import com.example.calendy.data.schedule.ScheduleRepository
import com.example.calendy.data.todo.TodoLocalDataSource
import com.example.calendy.data.todo.TodoRepository

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
    val categoryRepository : ICategoryRepository
    val messageRepository : IMessageRepository
}
class AppContainer(private val context: Context) : IAppContainer {
    override val scheduleRepository: IScheduleRepository by lazy {
        ScheduleRepository(ScheduleLocalDataSource(CalendyDatabase.getDatabase(context).scheduleDao()))
    }
    override val todoRepository: ITodoRepository by lazy {
        TodoRepository(TodoLocalDataSource(CalendyDatabase.getDatabase(context).todoDao()))
    }
    override val categoryRepository: ICategoryRepository by lazy {
        CategoryRepository(CategoryLocalDataSource(CalendyDatabase.getDatabase(context).categoryDao()))
    }
    override val messageRepository: IMessageRepository by lazy {
        MessageRepository(MessageLocalDataSource(CalendyDatabase.getDatabase(context).messageDao()))
    }
}