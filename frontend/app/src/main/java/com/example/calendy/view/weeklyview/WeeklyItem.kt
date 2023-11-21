package com.example.calendy.view.weeklyview

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.calendy.data.maindb.plan.PlanType
import com.example.calendy.data.maindb.plan.Schedule
import com.example.calendy.data.maindb.plan.Todo
import com.example.calendy.ui.theme.getColor
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt


@Composable
fun ScheduleItem(
    schedule: Schedule,
    modifier: Modifier = Modifier,
    onNavigateToEditPage: (id: Int?, type: PlanType, date: Date?) -> Unit
) {
    val clickAction = {
        onNavigateToEditPage(schedule.id, PlanType.SCHEDULE, null)
    }

//    val formatter = SimpleDateFormat("h:mm a", Locale.getDefault())
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(schedule.getColor())
            .clickable(onClick = clickAction)
            .padding()
    ) {
//        Text(
//            text = "${formatter.format(schedule.startTime)} - ${formatter.format(schedule.endTime)}",
//            style = MaterialTheme.typography.labelSmall,
//        )

        Text(
            text = schedule.title,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
        )

    }
}

@Composable
fun TodoItem(
    todo: Todo,
    modifier: Modifier = Modifier,
    onNavigateToEditPage: (id: Int?, type: PlanType, date: Date?) -> Unit
) {
    val clickAction = {
        onNavigateToEditPage(todo.id, PlanType.TODO, null)
    }
    Column() {

    }
    Card(
        shape = RoundedCornerShape(4.dp),
        modifier = modifier
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .clickable(onClick = clickAction)
            .background(todo.getColor())
    ) {
        Text(
            text = todo.title,
            modifier = Modifier.padding(8.dp),
        )
    }

}

@Composable
fun WeekHeader(
    startDate: Date,
    dayWidth: Dp,
    modifier: Modifier = Modifier,
) {
    val calendar = Calendar.getInstance()
    calendar.time = startDate
    val dayFormatter = SimpleDateFormat("E\nd", Locale.getDefault())

    Row(modifier = modifier) {
        val numDays = 7
        repeat(numDays) { _ ->
            val dateText = dayFormatter.format(calendar.time)
            Box(modifier = Modifier.width(dayWidth)) {
                Text(
                    text = dateText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                )
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
    }
}

@Composable
fun WeekSidebar(
    hourHeight: Dp,
    modifier: Modifier = Modifier,
) {
    val hourFormatter = SimpleDateFormat("HH", Locale.getDefault())
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    Column(modifier = modifier) {
        repeat(24) { _ ->
            val hourText = hourFormatter.format(calendar.time)
            Box(modifier = Modifier.height(hourHeight)) {
                Text(
                    text = hourText, modifier = modifier
                        .fillMaxHeight()
                        .padding(4.dp)
                )
            }
            calendar.add(Calendar.HOUR_OF_DAY, 1)
        }
    }
}

@Composable
fun WeeklyTable(
    uiState: WeeklyUiState,
    modifier: Modifier = Modifier,
    scheduleContent: @Composable (schedule: Schedule) -> Unit = {
        ScheduleItem(
            schedule = it,
            onNavigateToEditPage = onNavigateToEditPage
        )
    },
    todoContent: @Composable (todo: Todo) -> Unit = { TodoItem(todo = it, onNavigateToEditPage = onNavigateToEditPage) },
    dayWidth: Dp,
    hourHeight: Dp,
    onNavigateToEditPage: (id: Int?, type: PlanType, date: Date?) -> Unit
) {
    val schedules = uiState.weekSchedules
    val todos = uiState.weekTodos
    val numDays = 7
    val dividerColor = Color.LightGray

    Layout(
        content = {
            schedules.sortedBy(Schedule::startTime).forEach { item ->
                Box(modifier = Modifier.scheduleData(schedule = item)) {
                    scheduleContent(item)
                }
            }
//            todos.sortedBy(Todo::dueTime).forEach { todo ->
//                Box(modifier = Modifier.todoData(todo = todo)) {
//                    todoContent(todo)
//                }
//            }
        },
        modifier = modifier.drawBehind {
                repeat(23) {
                    drawLine(
                        dividerColor,
                        start = Offset(0f, (it + 1) * hourHeight.toPx()),
                        end = Offset(size.width, (it + 1) * hourHeight.toPx()),
                        strokeWidth = 1.dp.toPx()
                    )
                }
                repeat(numDays - 1) {
                    drawLine(
                        dividerColor,
                        start = Offset((it + 1) * dayWidth.toPx(), 0f),
                        end = Offset((it + 1) * dayWidth.toPx(), size.height),
                        strokeWidth = 1.dp.toPx()
                    )
                }
            },
    ) { measureables, constraints ->
        val height = (hourHeight*24).roundToPx()
        val width = (dayWidth*7).roundToPx()
        val scheduleMeasureables = measureables.filter { it.parentData is Schedule }
        val todoMeasureables = measureables.filter { it.parentData is Todo }
        val placeableWithSchedules = scheduleMeasureables.map { measurable ->
            val schedule = measurable.parentData as Schedule
            val itemDurationMinutes =
                TimeUnit.MILLISECONDS.toMinutes(schedule.endTime.time - schedule.startTime.time)
            if (itemDurationMinutes < 0) {
                Log.e("ScheduleError", "Negative duration found in schedule: $schedule")
            }
            val itemHeight = ((itemDurationMinutes / 60f) * hourHeight.toPx()).roundToInt()
            val placeable = measurable.measure(
                constraints.copy(
                    minWidth = dayWidth.roundToPx(),
                    maxWidth = dayWidth.roundToPx(),
                    minHeight = itemHeight,
                    maxHeight = itemHeight
                )
            )
            Pair(placeable, schedule)
        }
        val placeableWithTodos = todoMeasureables.map { measurable ->
            val todo = measurable.parentData as Todo
            val placeable = measurable.measure(
                constraints.copy(
                    minWidth = dayWidth.roundToPx(),
                    maxWidth = dayWidth.roundToPx(),
                )
            )
            Pair(placeable, todo)
        }
        layout(width, height) {
            placeableWithSchedules.forEach { (placeable, schedule) ->
                // schedule y좌표 정하기
                val calendar = Calendar.getInstance().apply {
                    time = schedule.startTime
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val midnight = calendar.time
                val itemOffsetMinutes =
                    TimeUnit.MILLISECONDS.toMinutes(schedule.startTime.time - midnight.time)
                val itemY = ((itemOffsetMinutes / 60f) * hourHeight.toPx()).roundToInt()

                // scheule x 좌표 정하기
                val startOfWeekCalendar = Calendar.getInstance().apply {
                    time = uiState.currentWeek.first
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val scheduleStartCalendar = Calendar.getInstance().apply {
                    time = schedule.startTime
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val itemOffsetDays =
                    TimeUnit.MILLISECONDS.toDays(scheduleStartCalendar.timeInMillis - startOfWeekCalendar.timeInMillis)
                        .toInt()
                val itemX = itemOffsetDays * dayWidth.roundToPx()
                // x,y 좌표 기반 배치
                placeable.place(itemX, itemY)
            }
            placeableWithTodos.forEach{(placeable, todo) ->
                // todo객체 x 좌표 정하기
                val startOfWeekCalendar = Calendar.getInstance().apply {
                    time = uiState.currentWeek.first
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val todoDayCalendar = Calendar.getInstance().apply {
                    time = todo.dueTime
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                val itemOffsetDays =
                    TimeUnit.MILLISECONDS.toDays(todoDayCalendar.timeInMillis - startOfWeekCalendar.timeInMillis)
                        .toInt()
                val itemX = itemOffsetDays * dayWidth.roundToPx()

                // x,y 좌표 기반 배치

            }
        }
    }
}

private class ScheduleDataModifier(
    val schedule: Schedule,
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?) = schedule
}

private fun Modifier.scheduleData(schedule: Schedule) = this.then(ScheduleDataModifier(schedule))
private class TodoDataModifier(
    val todo: Todo,
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?) = todo
}

private fun Modifier.todoData(todo: Todo) = this.then(TodoDataModifier(todo))


@Preview(showBackground = true, name = "WeekHeader Preview")
@Composable
fun WeekHeaderPreview() {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
    }
    WeekHeader(startDate = calendar.time, dayWidth = 256.dp)
}

@Preview(showBackground = true, name = "WeekSidebar Preview")
@Composable
fun WeekSidebarPreview() {
    WeekSidebar(hourHeight = 64.dp)
}


