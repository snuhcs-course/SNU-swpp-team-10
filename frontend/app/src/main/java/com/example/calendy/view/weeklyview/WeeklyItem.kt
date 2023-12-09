package com.example.calendy.view.weeklyview

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calendy.data.maindb.plan.PlanType
import com.example.calendy.data.maindb.plan.Schedule
import com.example.calendy.data.maindb.plan.Todo
import com.example.calendy.ui.theme.bottomBorder
import com.example.calendy.ui.theme.getColor
import com.example.calendy.utils.applyTime
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.roundToInt


@Composable
fun ScheduleItem(
    schedule: Schedule,
    modifier: Modifier = Modifier,
    onNavigateToEditPage: (Int?, PlanType, Date?, Date?) -> Unit
) {
    val clickAction = {
        onNavigateToEditPage(schedule.id, PlanType.SCHEDULE, null, null)
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                schedule
                    .getColor()
                    .copy(alpha = 0.6f), RoundedCornerShape(4.dp)
            )
            .clickable(onClick = clickAction)
            .padding(), contentAlignment = Alignment.Center
    ) {
        Text(
            text = schedule.title,
            style = MaterialTheme.typography.labelSmall,
//            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = if (schedule.priority >= 3) Color.White else Color.Black,
            maxLines = 2,
        )
    }
}

@Composable
fun TodoItem(
    viewModel: WeeklyViewModel,
    modifier: Modifier = Modifier,
    todo: Todo,
    tailHeight: Dp = 10.dp,
    onNavigateToEditPage: (Int?, PlanType, Date?, Date?) -> Unit
) {
    val clickAction = {
        onNavigateToEditPage(todo.id, PlanType.TODO, null, null)
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
        if (tailDirectionUp) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp) // 선의 높이
                    .padding(horizontal = 1.dp)
            ) { // 좌우 패딩
                val strokeWidth = 8.dp.toPx() // 선의 두께
                val y = center.y // 선을 그릴 y 위치
                drawLine(
                    color = todo.getColor().copy(alpha = 0.6f), // 선의 색상
                    start = Offset(strokeWidth, y), // 시작 위치
                    end = Offset(size.width - strokeWidth, y), // 끝 위치
                    strokeWidth = strokeWidth, cap = StrokeCap.Round // 라운드 모양의 선 끝 처리
                )
            }
            Spacer(modifier = Modifier.height(5.dp))
        }
        Card(shape = shape,
             colors = if (todo.complete) CardDefaults.cardColors(Color.White.copy(alpha = 0.6f)) else CardDefaults.cardColors(
                 containerColor = todo.getColor().copy(alpha = 0.6f)
             ),
             border = if (todo.complete) BorderStroke(
                 width = 3.dp, color = todo.getColor().copy(alpha = 0.8f)
             ) else null,
             modifier = modifier
                 .fillMaxWidth()
                 .height(45.dp)
                 .pointerInput(todo) {
                     detectTapGestures(
                         onTap = {
                             clickAction()
                         },
                         onLongPress = {
                             viewModel.updateCompletionOfTodo(todo)
                         },

                         )
                 }

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
            ) {
                Text(
                    text = todo.title,
                    modifier = Modifier.padding(1.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                    textDecoration = if (todo.complete) TextDecoration.LineThrough else null,
                    color = if (todo.complete) Color.Gray else if(todo.priority>=3) Color.White else Color.Black
                )
            }
        }
        if (!tailDirectionUp) {
            Spacer(modifier = Modifier.height(5.dp))
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp) // 선의 높이
                    .padding(horizontal = 1.dp)
            ) { // 좌우 패딩
                val strokeWidth = 8.dp.toPx() // 선의 두께
                val y = center.y // 선을 그릴 y 위치
                drawLine(
                    color = todo.getColor().copy(alpha = 0.6f), // 선의 색상
                    start = Offset(strokeWidth, y), // 시작 위치
                    end = Offset(size.width - strokeWidth, y), // 끝 위치
                    strokeWidth = strokeWidth, cap = StrokeCap.Round // 라운드 모양의 선 끝 처리
                )
            }
        }
    }
}


@Composable
fun WeekHeader(
    uiState: WeeklyUiState,
    dayWidth: Dp,
    modifier: Modifier = Modifier,
    onNavigateToEditPage: (id: Int?, type: PlanType, startDate: Date?, endDate: Date?) -> Unit,
) {
    val multipleDaySchedules: List<Schedule> = uiState.multipleDaySchedules.filterNot { schedule ->
        isSameDay(
            schedule.startTime,
            schedule.endTime
        )
    }
    val calendar = Calendar.getInstance()
    calendar.time = uiState.currentWeek.first
    val dayFormatter = SimpleDateFormat("E\nd", Locale.getDefault())
    Column(modifier = Modifier.bottomBorder(0.5f.dp, Color.LightGray)) {
        Row(modifier = modifier.bottomBorder(0.5f.dp, Color.LightGray)) {
            val numDays = 7
            repeat(numDays) { _ ->
                val dateText = dayFormatter.format(calendar.time)
                Box(modifier = Modifier.width(dayWidth)) {
                    Text(
                        text = dateText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        fontSize = 12.sp,
                        color = when (calendar.get(Calendar.DAY_OF_WEEK)) {
                            Calendar.SUNDAY   -> Color.Red
                            Calendar.SATURDAY -> Color.Blue
                            else              -> Color.Black
                        }
                    )
                }
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }
        }
//        if(multipleDaySchedules.isNotEmpty()){
        LongPlanStack(
            modifier = modifier,
            uiState = uiState,
            dayWidth = dayWidth,
            onNavigateToEditPage = onNavigateToEditPage
        )
//        }

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

    Column(modifier = modifier.border(0.5f.dp, Color.LightGray)) {
        repeat(24) { _ ->
            val hourText = hourFormatter.format(calendar.time)
            Box(modifier = Modifier
                .height(hourHeight)
                .bottomBorder((0.5f).dp, Color.LightGray)) {
                Text(
                    text = hourText,
                    modifier = modifier
                        .fillMaxHeight()
                        .padding(4.dp),
                    fontSize = 12.sp,
                )
            }
            calendar.add(Calendar.HOUR_OF_DAY, 1)
        }
    }
}

@Composable
fun WeeklyTable(
    uiState: WeeklyUiState,
    viewModel: WeeklyViewModel,
    modifier: Modifier = Modifier,
    scheduleContent: @Composable (schedule: Schedule) -> Unit = {
        ScheduleItem(
            schedule = it, onNavigateToEditPage = onNavigateToEditPage
        )
    },
    todoContent: @Composable (todo: Todo) -> Unit = {
        TodoItem(
            viewModel = viewModel, todo = it, onNavigateToEditPage = onNavigateToEditPage
        )
    },
    dayWidth: Dp,
    hourHeight: Dp,
    onNavigateToEditPage: (Int?, PlanType, Date?, Date?) -> Unit
) {
    val schedules =
        uiState.weekSchedules.filter { schedule -> isSameDay(schedule.startTime, schedule.endTime) }
    val todos = uiState.weekTodos
    val numDays = 7
    val dividerColor = Color.LightGray

    BoxWithConstraints(modifier = modifier) {
        // 클릭 가능한 시간 슬롯 Box 배치
        for (day in 0 until numDays) {
            for (hour in 0 until 24) {
                ClickableTimeSlotBox(
                    uiState = uiState,
                    day = day,
                    hour = hour,
                    dayWidth = dayWidth,
                    hourHeight = hourHeight,
                    onNavigateToEditPage = onNavigateToEditPage
                )
            }
        }
        Layout(
            content = {
                schedules.filter { s ->
                    s.startTime.before(uiState.currentWeek.second) && s.endTime.after(
                        uiState.currentWeek.first
                    )
                }.sortedBy(Schedule::startTime).forEach { schedule ->
                        Box(modifier = Modifier.scheduleData(schedule = schedule)) {
                            scheduleContent(schedule)
                        }
                    }
                todos.filter { t ->
                    t.dueTime.before(uiState.currentWeek.second) && t.dueTime.after(
                        uiState.currentWeek.first
                    )
                }.sortedBy(Todo::dueTime).forEach { todo ->
                        Box(modifier = Modifier.todoData(todo = todo)) {
                            todoContent(todo)
                        }
                    }
            },
            modifier = modifier
                .matchParentSize()
                .drawBehind {
                    repeat(23) {
                        drawLine(
                            dividerColor,
                            start = Offset(0f, (it + 1) * hourHeight.toPx()),
                            end = Offset(size.width, (it + 1) * hourHeight.toPx()),
                            strokeWidth = 0.5f.dp.toPx()
                        )
                    }
                    repeat(numDays - 1) {
                        drawLine(
                            dividerColor,
                            start = Offset((it + 1) * dayWidth.toPx(), 0f),
                            end = Offset((it + 1) * dayWidth.toPx(), size.height),
                            strokeWidth = 0.5f.dp.toPx()
                        )
                    }
                },

            ) { measureables, constraints ->
            val height = (hourHeight * 24).roundToPx()
            val width = (dayWidth * 7).roundToPx()
            val scheduleMeasureables = measureables.filter { it.parentData is Schedule }
            val todoMeasureables = measureables.filter { it.parentData is Todo }
            val placeableWithSchedules = scheduleMeasureables.map { measurable ->
                val schedule = measurable.parentData as Schedule
                val itemHeight =
                    ((TimeUnit.MILLISECONDS.toMinutes(schedule.endTime.time - schedule.startTime.time) / 60f) * hourHeight.toPx()).roundToInt()
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
                    val itemX = itemOffsetDays * dayWidth.roundToPx()
                    placeable.place(itemX, itemY)
                }
                placeableWithTodos.forEach { (placeable, todo) ->
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

                    val itemY = if (dueTimeCheck) {
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
fun ClickableTimeSlotBox(
    uiState: WeeklyUiState,
    day: Int,
    hour: Int,
    dayWidth: Dp,
    hourHeight: Dp,
    onNavigateToEditPage: (Int?, PlanType, Date?, Date?) -> Unit
) {
    val boxX = day * dayWidth.value
    val boxY = hour * hourHeight.value

    val clickedDateTime = Calendar.getInstance().apply {
        time = uiState.currentWeek.first
        add(Calendar.DAY_OF_YEAR, day)
        set(Calendar.HOUR_OF_DAY, hour)
    }.time
    val endDate = clickedDateTime.applyTime(hour + 1, 0)

    Box(modifier = Modifier
        .offset(x = boxX.dp, y = boxY.dp)
        .size(dayWidth, hourHeight)
        .clickable {
            onNavigateToEditPage(null, PlanType.SCHEDULE, clickedDateTime, endDate)
        }) {

    }
}

@Composable
fun LongPlanStack(
    modifier: Modifier = Modifier,
    uiState: WeeklyUiState,
    dayWidth: Dp,
    onNavigateToEditPage: (id: Int?, type: PlanType, startDate: Date?, endDate: Date?) -> Unit,
    scheduleContent: @Composable (schedule: Schedule) -> Unit = {
        ScheduleItem(
            schedule = it,
            onNavigateToEditPage = onNavigateToEditPage,
            modifier = Modifier.background(
                it.getColor(), RoundedCornerShape(4.dp)
            )
        )
    }
) {
    val schedules = uiState.multipleDaySchedules
    val currentWeek = uiState.currentWeek

    Layout(modifier = modifier
        , content = {
        schedules.sortedBy(Schedule::startTime).forEach { schedule ->
            Box(modifier = Modifier.scheduleData(schedule = schedule)) {
                scheduleContent(schedule)
            }
        }

    }) { measureables, constraints ->
        val width = (dayWidth * 7).roundToPx()
        val schedulesByDayOfWeek = IntArray(7) { 0 }
        schedules.forEach { schedule ->
            val dayOfWeek = Calendar.getInstance()
                .apply { time = schedule.startTime }
                .get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY
            val duration = calculateDateDifference(
                schedule.startTime,
                schedule.endTime,
                currentWeek.first,
                currentWeek.second
            )
            for (i: Int in dayOfWeek until dayOfWeek + duration) {
                schedulesByDayOfWeek[i]++
            }
        }
        val maxOverlap = schedulesByDayOfWeek.maxOrNull() ?: 0
        val height = maxOverlap * 45
        val placeableWithSchedules = measureables.map { measurable ->
            val schedule = measurable.parentData as Schedule
            val itemWidth = dayWidth * calculateDateDifference(
                schedule.startTime, schedule.endTime, currentWeek.first, currentWeek.second
            )
            val placeable = measurable.measure(
                constraints.copy(
                    minWidth = max((itemWidth - 3.dp).roundToPx(), 0),
                    maxWidth = max((itemWidth - 3.dp).roundToPx(), 0),
                    minHeight = 40,
                    maxHeight = 40,
                )
            )
            Pair(placeable, schedule)
        }
        layout(width, height) {
            val itemYPositions = mutableMapOf<Int, MutableList<Boolean>>().apply {
                for (dayOfWeek in 0..6) { // 요일별로 초기화
                    this[dayOfWeek] = MutableList(height / 43) { false }
                }
            }
            placeableWithSchedules.forEach { (placeable, schedule) ->
                val start =
                    if (schedule.startTime.before(currentWeek.first)) currentWeek.first else schedule.startTime
                val offset = Calendar.getInstance()
                    .apply { time = start }
                    .get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY
                val duration = calculateDateDifference(
                    schedule.startTime,
                    schedule.endTime,
                    currentWeek.first,
                    currentWeek.second
                )
                val itemX = ((dayWidth * offset) + 1.5.dp).roundToPx()
                val itemYList = itemYPositions[offset]!!
                val itemYIndex = itemYList.indexOfFirst { !it }
                val itemY = itemYIndex * 45
                placeable.place(itemX, itemY)
                for (i: Int in offset until offset + duration) {
                    itemYPositions[i]!![itemYIndex] = true
                }
            }
        }
    }

}

fun calculateDateDifference(start: Date, end: Date, weekStartDate: Date, weekLastDate: Date): Int {
    val calendar = Calendar.getInstance()
    if (start.after(weekLastDate) || end.before(weekStartDate)) {
        return 0
    }

    val startDate = if (start.after(weekStartDate)) start else weekStartDate
    calendar.time = startDate
    val startDayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
    val startYear = calendar.get(Calendar.YEAR)

    val endDate = if (end.before(weekLastDate)) end else weekLastDate
    calendar.time = endDate
    val endHour = calendar.get(Calendar.HOUR_OF_DAY)
    val endMinutes = calendar.get(Calendar.MINUTE)
    val endSeconds = calendar.get(Calendar.SECOND)
    val endMilliseconds = calendar.get(Calendar.MILLISECOND)

    val isEndDateAtMidnight = endHour==0 && endMinutes==0 && endSeconds==0 && endMilliseconds==0
    if (isEndDateAtMidnight) {
        calendar.add(Calendar.DATE, -1)
    }
    val endDayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
    val endYear = calendar.get(Calendar.YEAR)

    return if (startYear==endYear) {
        endDayOfYear - startDayOfYear + 1
    } else {
        var totalDays = 0

        for (year in startYear until endYear) {
            calendar.set(Calendar.YEAR, year)
            totalDays += if (calendar.getActualMaximum(Calendar.DAY_OF_YEAR) > 365) 366 else 365
        }
        totalDays - startDayOfYear + endDayOfYear + 1
    }
}

fun isSameDay(date1: Date, date2: Date): Boolean {
    val cal1 = Calendar.getInstance().apply { time = date1 }
    val cal2 = Calendar.getInstance().apply { time = date2 }

    // date2가 자정인지 확인
    val isMidnight =
        cal2.get(Calendar.HOUR_OF_DAY)==0 && cal2.get(Calendar.MINUTE)==0 && cal2.get(Calendar.SECOND)==0 && cal2.get(
            Calendar.MILLISECOND
        )==0

    // 두 날짜가 같은 해, 같은 일인지 확인
    val isSameDay =
        cal1.get(Calendar.YEAR)==cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR)==cal2.get(
            Calendar.DAY_OF_YEAR
        )

    if (isSameDay) {
        return true
    }

    // date2가 자정이고 date1보다 하루 뒤인 경우
    if (isMidnight) {
        val cal1NextDay = Calendar.getInstance().apply {
            time = date1
            add(Calendar.DAY_OF_YEAR, 1)
        }
        return cal1NextDay.get(Calendar.YEAR)==cal2.get(Calendar.YEAR) && cal1NextDay.get(Calendar.DAY_OF_YEAR)==cal2.get(
            Calendar.DAY_OF_YEAR
        )
    }

    return false
}


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
            size: Size, layoutDirection: LayoutDirection, density: Density
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


@Preview(showBackground = true, name = "Header Preview")
@Composable
fun HeaderPreview() {
    WeekHeader(uiState = WeeklyUiState(),
               dayWidth = 64.dp,
               onNavigateToEditPage = { _, _, _, _ -> })
}

@Preview(showBackground = true, name = "WeekSidebar Preview")
@Composable
fun WeekSidebarPreview() {
    WeekSidebar(hourHeight = 64.dp)
}


