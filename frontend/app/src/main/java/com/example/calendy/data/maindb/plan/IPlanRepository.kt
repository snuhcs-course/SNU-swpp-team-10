package com.example.calendy.data.maindb.plan

import androidx.sqlite.db.SupportSQLiteQuery
import com.example.calendy.data.BaseRepository
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface IPlanRepository : BaseRepository<Plan> {
    fun getAllPlansStream(): Flow<List<Plan>>
    fun getPlansStream(startTime: Date, endTime: Date): Flow<List<Plan>>
    fun getPlanById(id: Int, type: PlanType): Plan
    fun getSchedulesViaQuery(query: SupportSQLiteQuery): List<Schedule>
    fun getTodosViaQuery(query: SupportSQLiteQuery): List<Todo>

    //TODO : check if this suspend is validn
    // Comment from GUN: suspend 없앴습니다
    fun getPlansByIds(scheduleIDs: List<Int>, todoIDs: List<Int>): List<Plan>
}