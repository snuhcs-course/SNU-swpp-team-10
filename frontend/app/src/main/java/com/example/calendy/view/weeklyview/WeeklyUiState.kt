package com.example.calendy.view.weeklyview

import com.example.calendy.data.maindb.plan.Plan
import com.example.calendy.data.maindb.plan.Schedule
import com.example.calendy.data.maindb.plan.Todo
import java.util.Calendar
import java.util.Date

data class WeeklyUiState(
    val weekSchedules : List<Schedule> = emptyList(),
    val multipleDaySchedules : List<Schedule> = emptyList(),
    val weekTodos: List<Todo> = emptyList(),
    val pageCount : Int = Int.MAX_VALUE,             // 최대 페이지 개수
    val currentPosition : Int = Int.MAX_VALUE / 2 ,
    val currentWeek : Pair<Date, Date> = getCurrentWeek() // 주의 시작 날짜와 마지막 날짜
)

fun getCurrentWeek(): Pair<Date, Date> {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val startOfWeek = calendar.time
    calendar.add(Calendar.DAY_OF_WEEK, 6)
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    val endOfWeek = calendar.time

    return Pair(startOfWeek, endOfWeek)
}