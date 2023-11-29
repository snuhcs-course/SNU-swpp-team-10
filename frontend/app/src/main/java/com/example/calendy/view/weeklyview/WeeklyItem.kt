package com.example.calendy.view.weeklyview

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.calendy.data.maindb.plan.PlanType
import com.example.calendy.data.maindb.plan.Schedule
import com.example.calendy.data.maindb.plan.Todo
import com.example.calendy.ui.theme.getColor
import java.lang.Integer.max
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
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(schedule.getColor().copy(alpha = 0.6f))
            .clickable(onClick = clickAction)
            .padding(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = schedule.title,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun TodoItem(
    modifier: Modifier = Modifier,
    todo: Todo,
    tailHeight: Dp = 10.dp,
    onNavigateToEditPage: (id: Int?, type: PlanType, date: Date?) -> Unit
) {
    val clickAction = {
        onNavigateToEditPage(todo.id, PlanType.TODO, null)
    }
    // duetime이 자정 ~ am 1:30인 경우 말풍선을 밑쪽으로 배치
    val calendar = Calendar.getInstance().apply {
        time = todo.dueTime
    }
    val tailDirectionUp = when (calendar.get(Calendar.HOUR_OF_DAY)) {
        0    -> true // Between AM 0:00 and AM 0:59
        1    -> calendar.get(Calendar.MINUTE) <= 20 // Between AM 1:00 and AM 1:20
        else -> false
    }

    val shape = balloonShape(tailHeight = tailHeight, tailDirectionUp = tailDirectionUp)

    Column {
        if(tailDirectionUp) {
            Canvas(modifier = Modifier
                .fillMaxWidth()
                .height(1.dp) // 선의 높이
                .padding(horizontal = 1.dp)) { // 좌우 패딩
                val strokeWidth = 8.dp.toPx() // 선의 두께
                val y = center.y // 선을 그릴 y 위치
                drawLine(
                    color = todo.getColor().copy(alpha = 0.6f), // 선의 색상
                    start = Offset(strokeWidth, y), // 시작 위치
                    end = Offset(size.width - strokeWidth, y), // 끝 위치
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round // 라운드 모양의 선 끝 처리
                )
            }
            Spacer(modifier = Modifier.height(5.dp))
        }
        Card(
            shape = shape,
            colors = CardDefaults.cardColors(containerColor = todo.getColor().copy(alpha = 0.6f)),
            modifier = modifier
                .fillMaxWidth()
                .clickable(onClick = clickAction)
                .height(45.dp)

        ) {
            Box(
                contentAlignment = Alignment.Center, // Box 내부에서 컨텐츠를 중앙 정렬
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp - tailHeight) // 꼬리 높이를 뺀 나머지 높이
                    .then(
                        if (tailDirectionUp) {
                            Modifier.padding(top = tailHeight) // 꼬리가 위쪽인 경우 위쪽에 패딩을 추가
                        } else {
                            Modifier.padding(bottom = tailHeight) // 꼬리가 아래쪽인 경우 아래쪽에 패딩을 추가
                        }
                    )
            ){
                Text(
                    text = todo.title,
                    modifier = Modifier.padding(1.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        if(!tailDirectionUp) {
            Spacer(modifier = Modifier.height(5.dp))
            Canvas(modifier = Modifier
                .fillMaxWidth()
                .height(1.dp) // 선의 높이
                .padding(horizontal = 1.dp)) { // 좌우 패딩
                val strokeWidth = 8.dp.toPx() // 선의 두께
                val y = center.y // 선을 그릴 y 위치
                drawLine(
                    color = todo.getColor().copy(alpha = 0.6f), // 선의 색상
                    start = Offset(strokeWidth, y), // 시작 위치
                    end = Offset(size.width - strokeWidth, y), // 끝 위치
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round // 라운드 모양의 선 끝 처리
                )
            }
        }
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
            schedules.sortedBy(Schedule::startTime).forEach { originalSchedule ->
                // 일정을 날짜별로 분할
                val splitSchedules = splitScheduleByDays(originalSchedule, uiState.currentWeek.first, uiState.currentWeek.second)

                // 각 분할된 일정에 대해 scheduleContent를 호출
                splitSchedules.forEach { schedule ->
                    Box(modifier = Modifier.scheduleData(schedule = schedule)) {
                        scheduleContent(schedule)
                    }
                }
            }
            todos.sortedBy(Todo::dueTime).forEach { todo ->
                Box(modifier = Modifier.todoData(todo = todo)) {
                    todoContent(todo)
                }
            }
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
            val itemHeight = ((TimeUnit.MILLISECONDS.toMinutes(schedule.endTime.time - schedule.startTime.time) / 60f) * hourHeight.toPx()).roundToInt()
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
                // schedule 객체 y 좌표 정하기
                val startDayMidnight = Calendar.getInstance().apply {
                    time = schedule.startTime
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val itemOffsetMinutes =
                    TimeUnit.MILLISECONDS.toMinutes(schedule.startTime.time - startDayMidnight.time.time)
                val itemY = ((itemOffsetMinutes / 60f) * hourHeight.toPx()).roundToInt()

                // schedule 객체 x 좌표 정하기
                val startOfWeekCalendar = Calendar.getInstance().apply {
                    time = uiState.currentWeek.first
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val startDayCalendar = Calendar.getInstance().apply {
                    time = schedule.startTime
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                val itemOffsetDays =
                    TimeUnit.MILLISECONDS.toDays(startDayCalendar.timeInMillis - startOfWeekCalendar.timeInMillis)
                        .toInt()
                var itemX = itemOffsetDays * dayWidth.roundToPx()
                placeable.place(itemX, itemY)
            }
            placeableWithTodos.forEach{(placeable, todo) ->
                // todo y좌표 정하기
                val calendar = Calendar.getInstance().apply {
                    time = todo.dueTime
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val dueTime = Calendar.getInstance().apply {
                    time = todo.dueTime
                }
                val dueTimeCheck = when (dueTime.get(Calendar.HOUR_OF_DAY)) {
                    0    -> true // Between AM 0:00 and AM 0:59
                    1    -> calendar.get(Calendar.MINUTE) <= 20 // Between AM 1:00 and AM 1:20
                    else -> false
                }
                val midnight = calendar.time
                val itemOffsetMinutes =
                    TimeUnit.MILLISECONDS.toMinutes(todo.dueTime.time - midnight.time)

                val itemY = if(dueTimeCheck){
                    ((itemOffsetMinutes / 60f) * hourHeight.toPx()).roundToInt()
                } else {
                    (((itemOffsetMinutes / 60f)) * hourHeight.toPx()).roundToInt() - placeable.height
                }
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
                placeable.place(itemX, itemY)
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

@Composable
fun balloonShape(
    tailDirectionUp: Boolean = false,
    cornerRadius: Dp = 5.dp, // 모서리의 둥근 정도
    tailWidth: Dp = 10.dp, // 꼬리의 너비
    tailHeight: Dp = 10.dp, // 꼬리의 높이
    tailPositionPercent: Float = 0.5f, // 꼬리 위치 비율, 0.0 ~ 1.0 사이의 값
): Shape {

    return object : Shape {
        override fun createOutline(
            size: Size,
            layoutDirection: LayoutDirection,
            density: Density
        ): Outline {

            val path = Path()
            val cornerRadiusPx = with(density) { cornerRadius.toPx() }
            val tailWidthPx = with(density) { tailWidth.toPx() }
            val tailHeightPx = with(density) { tailHeight.toPx() }
            val tailPositionPx = size.width * tailPositionPercent

            // 사각형 상단 좌우 둥근 부분 그리기
            path.addRoundRect(
                RoundRect(
                    left = 0f,
                    top = if (tailDirectionUp) tailHeightPx else 0f,
                    right = size.width,
                    bottom = if (tailDirectionUp) size.height else size.height - tailHeightPx,
                    cornerRadius = CornerRadius(cornerRadiusPx)
                )
            )

            // 꼬리 그리기
            if (tailDirectionUp) {
                // 꼬리가 위쪽을 향할 때
                path.moveTo(tailPositionPx - tailWidthPx / 2, tailHeightPx)
                path.lineTo(tailPositionPx, 0f)
                path.lineTo(tailPositionPx + tailWidthPx / 2, tailHeightPx)
            } else {
                // 꼬리가 아래쪽을 향할 때
                path.moveTo(tailPositionPx - tailWidthPx / 2, size.height - tailHeightPx)
                path.lineTo(tailPositionPx, size.height)
                path.lineTo(tailPositionPx + tailWidthPx / 2, size.height - tailHeightPx)
            }

            return Outline.Generic(path)
        }
    }
}

// 기존 Schedule 객체를 날짜별로 분할하는 함수
fun splitScheduleByDays(schedule: Schedule, weekStart: Date, weekEnd: Date): List<Schedule> {
    val splitSchedules = mutableListOf<Schedule>()
    val startCalendar = Calendar.getInstance().apply { time = schedule.startTime }
    val endCalendar = Calendar.getInstance().apply { time = schedule.endTime }



    val weekEndCalendar = Calendar.getInstance()
    weekEndCalendar.time = weekEnd
    weekEndCalendar.add(Calendar.DATE, 1)
    weekEndCalendar.set(Calendar.HOUR_OF_DAY, 0)
    weekEndCalendar.set(Calendar.MINUTE, 0)
    weekEndCalendar.set(Calendar.SECOND, 0)
    weekEndCalendar.set(Calendar.MILLISECOND,1)

    while (!startCalendar.after(endCalendar)) {
        val nextDayCalendar = startCalendar.clone() as Calendar
        nextDayCalendar.add(Calendar.DATE, 1)
        nextDayCalendar.set(Calendar.HOUR_OF_DAY, 0)
        nextDayCalendar.set(Calendar.MINUTE, 0)
        nextDayCalendar.set(Calendar.SECOND, 0)
        nextDayCalendar.set(Calendar.MILLISECOND, 0)


        val partEnd = if (endCalendar.before(nextDayCalendar)) endCalendar.time else nextDayCalendar.time

        // 일정이 주 내에 있는 경우에만 추가
        if (!startCalendar.time.before(weekStart) && partEnd.before(weekEndCalendar.time)) {
            splitSchedules.add(
                schedule.copy(
                    startTime = startCalendar.time,
                    endTime = partEnd
                )
            )
        }
        // 다음 날짜로 이동
        startCalendar.time = nextDayCalendar.time
    }

    return splitSchedules
}


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


