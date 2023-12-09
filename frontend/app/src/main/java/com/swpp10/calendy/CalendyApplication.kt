package com.swpp10.calendy

import android.app.Application
import android.content.Context
import androidx.work.Configuration
import com.swpp10.calendy.data.maindb.CalendyDatabase
import com.swpp10.calendy.data.maindb.category.CategoryRepository
import com.swpp10.calendy.data.maindb.category.ICategoryRepository
import com.swpp10.calendy.data.maindb.history.HistoryRepository
import com.swpp10.calendy.data.maindb.history.IHistoryRepository
import com.swpp10.calendy.data.maindb.message.IMessageRepository
import com.swpp10.calendy.data.maindb.message.MessageRepository
import com.swpp10.calendy.data.maindb.plan.IPlanRepository
import com.swpp10.calendy.data.maindb.plan.PlanRepository
import com.swpp10.calendy.data.maindb.plan.schedule.IScheduleRepository
import com.swpp10.calendy.data.maindb.plan.schedule.ScheduleRepository
import com.swpp10.calendy.data.maindb.plan.todo.ITodoRepository
import com.swpp10.calendy.data.maindb.plan.todo.TodoRepository
import com.swpp10.calendy.data.maindb.rawplan.RawPlanRepository
import com.swpp10.calendy.data.maindb.repeatgroup.IRepeatGroupRepository
import com.swpp10.calendy.data.maindb.repeatgroup.RepeatGroupRepository
import com.swpp10.calendy.data.network.CalendyServerApi
import com.swpp10.calendy.data.network.RetrofitClient

import com.swpp10.calendy.view.messageview.CustomWorkerFactory

class CalendyApplication : Application(), Configuration.Provider {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }

    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder().setWorkerFactory(
            CustomWorkerFactory(
                calendyDatabase = container.db,
                calendyServerApi = container.calendyServerApi,
                messageRepository = container.messageRepository,
                planRepository = container.planRepository,
                historyRepository = container.historyRepository,
                categoryRepository = container.categoryRepository,
                rawPlanRepository = container.rawPlanRepository
            )
        ).build()
}

interface IAppContainer {
    val db: CalendyDatabase
    val planRepository: IPlanRepository
    val scheduleRepository: IScheduleRepository
    val todoRepository: ITodoRepository
    val categoryRepository: ICategoryRepository
    val messageRepository: IMessageRepository
    val repeatGroupRepository: IRepeatGroupRepository
    val historyRepository: IHistoryRepository
    val rawPlanRepository: RawPlanRepository
    val calendyServerApi: CalendyServerApi // Network
}

class AppContainer(private val context: Context) : IAppContainer {
    override val db: CalendyDatabase by lazy { CalendyDatabase.getDatabase(context) }

    override val planRepository: IPlanRepository by lazy {
        PlanRepository(
            scheduleRepository = scheduleRepository,
            todoRepository = todoRepository,
            categoryRepository = categoryRepository,
            repeatGroupRepository = repeatGroupRepository
        )
    }

    // TODO: Don't expose scheduleRepository
    override val scheduleRepository: IScheduleRepository by lazy {
        ScheduleRepository(db.scheduleDao())
    }
    override val todoRepository: ITodoRepository by lazy {
        TodoRepository(db.todoDao())
    }
    override val categoryRepository: ICategoryRepository by lazy {
        CategoryRepository(db.categoryDao())
    }
    override val messageRepository: IMessageRepository by lazy {
        MessageRepository(db.messageDao())
    }
    override val repeatGroupRepository: IRepeatGroupRepository by lazy {
        RepeatGroupRepository(db.repeatGroupDao())
    }
    override val historyRepository: IHistoryRepository by lazy {
        HistoryRepository(db.managerHistoryDao(), db.savedScheduleDao(), db.savedTodoDao())
    }
    override val rawPlanRepository: RawPlanRepository by lazy {
        RawPlanRepository(rawScheduleDao = db.rawScheduleDao(), rawTodoDao = db.rawTodoDao())
    }

    override val calendyServerApi: CalendyServerApi by lazy {
        RetrofitClient.instance
    }

}