package com.example.calendy.view.messageview

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.calendy.data.maindb.CalendyDatabase
import com.example.calendy.data.maindb.category.ICategoryRepository
import com.example.calendy.data.maindb.history.IHistoryRepository
import com.example.calendy.data.maindb.message.IMessageRepository
import com.example.calendy.data.maindb.plan.IPlanRepository
import com.example.calendy.data.network.CalendyServerApi
import com.example.calendy.data.rawsqldb.RawSqlDatabase

class CustomWorkerFactory(
    private val calendyDatabase: CalendyDatabase,
    private val planRepository: IPlanRepository,
    private val categoryRepository: ICategoryRepository,
    private val messageRepository: IMessageRepository,
    private val historyRepository: IHistoryRepository,
    private val calendyServerApi: CalendyServerApi,
    private val rawSqlDatabase: RawSqlDatabase
) : WorkerFactory() {
    /**
     * Override this method to implement your custom worker-creation logic.  Use
     * [Configuration.Builder.setWorkerFactory] to use your custom class.
     *
     *
     * Throwing an [Exception] here will crash the application. If a [WorkerFactory]
     * is unable to create an instance of the [ListenableWorker], it should return `null` so it can delegate to the default [WorkerFactory].
     *
     *
     * Returns a new instance of the specified `workerClassName` given the arguments.  The
     * returned worker must be a newly-created instance and must not have been previously returned
     * or invoked by WorkManager. Otherwise, WorkManager will throw an
     * [IllegalStateException].
     *
     * @param appContext The application context
     * @param workerClassName The class name of the worker to create
     * @param workerParameters Parameters for worker initialization
     * @return A new [ListenableWorker] instance of type `workerClassName`, or
     * `null` if the worker could not be created
     */
    override fun createWorker(
        appContext: Context, workerClassName: String, workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            SendMessageWorker::class.java.name   -> {
                SendMessageWorker(
                    appContext,
                    workerParameters,
                    calendyServerApi = calendyServerApi,
                    messageRepository = messageRepository,
                    planRepository = planRepository,
                    historyRepository = historyRepository,
                    categoryRepository = categoryRepository,
                    rawSqlDatabase = rawSqlDatabase
                )
            }

            UATDataWorker::class.java.name -> {
                UATDataWorker(
                    appContext,
                    workerParameters,
                    calendyDatabase = calendyDatabase,
                    planRepository = planRepository,
                    categoryRepository = categoryRepository,
                )
            }
            // Add other workers as needed
            else                                 -> null
        }
    }
}