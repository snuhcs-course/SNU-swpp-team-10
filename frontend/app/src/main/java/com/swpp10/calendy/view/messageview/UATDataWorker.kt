package com.swpp10.calendy.view.messageview

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.swpp10.calendy.data.maindb.CalendyDatabase
import com.swpp10.calendy.data.maindb.category.Category
import com.swpp10.calendy.data.maindb.category.ICategoryRepository
import com.swpp10.calendy.data.maindb.plan.IPlanRepository
import com.swpp10.calendy.data.maindb.plan.Schedule
import com.swpp10.calendy.data.maindb.plan.Todo
import com.swpp10.calendy.utils.DateHelper

class UATDataWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val calendyDatabase: CalendyDatabase,
    private val planRepository: IPlanRepository,
    private val categoryRepository: ICategoryRepository,
) : CoroutineWorker(appContext, workerParams) {
    // Categories
    private val testId = 1
    private val assignmentId = 2
    private val meetingId = 3
    private val hobbyId = 4

    override suspend fun doWork(): Result {
        // NOTE: PrimaryKey.autogenerate is not reset to 0
        calendyDatabase.clearAllTables()

        categoryRepository.insert(
            Category(
                id = testId, title = "시험", defaultPriority = 5
            )
        )
        categoryRepository.insert(
            Category(
                id = assignmentId, title = "과제", defaultPriority = 4
            )
        )
        categoryRepository.insert(
            Category(
                id = meetingId, title = "약속", defaultPriority = 3
            )
        )
        categoryRepository.insert(
            Category(
                id = hobbyId, title = "취미", defaultPriority = 2
            )
        )

        planRepository.insert(
            Schedule(
                title = "민석이랑 점심",
                startTime = DateHelper.getDate(2023, 12 - 1, 15, 12, 0),
                endTime = DateHelper.getDate(2023, 12 - 1, 15, 13, 0),
                categoryId = meetingId,
                priority = 0 // This is for test
            )
        )
        planRepository.insert(
            Todo(
                title = "기타 연습",
                dueTime = DateHelper.getDate(2023, 12 - 1, 7, 23, 59),
                categoryId = hobbyId,
                priority = 0 // This is for test
            )
        )
        planRepository.insert(
            Todo(
                title = "글쓰기 과제",
                dueTime = DateHelper.getDate(2023, 12 - 1, 9, 18, 0),
                categoryId = assignmentId,
                priority = 0 // This is for test
            )
        )

        return Result.success()
    }

}