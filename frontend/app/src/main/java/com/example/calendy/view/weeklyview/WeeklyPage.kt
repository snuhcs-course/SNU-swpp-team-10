package com.example.calendy.view.weeklyview

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowLeft
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.calendy.data.maindb.plan.PlanType
import com.example.calendy.data.maindb.plan.Schedule
import com.example.calendy.data.maindb.plan.Todo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WeeklyPage(
    viewModel: WeeklyViewModel,
    onNavigateToEditPage: (id: Int?, type: PlanType, date: Date?) -> Unit
) {
    val uiState: WeeklyUiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = uiState.currentPosition)

    // 페이지 변화 시 uiState 값 업데이트
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { currentPage ->
            if (currentPage > uiState.currentPosition) {
                viewModel.increaseCurrentWeek()
            } else if (currentPage < uiState.currentPosition) {
                viewModel.decreaseCurrentWeek()
            }
            viewModel.updatePosition(currentPage)
            viewModel.updateWeekPlans()
        }
    }

    Scaffold(
        topBar = {
            Header(uiState = uiState,
                   coroutineScope = coroutineScope,
                   pagerState = pagerState,
                   onPreviousWeekClick = {},
                   onNextWeekClick = {})
        },

    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            WeekPager(
                pagerState = pagerState,
                uiState = uiState,
                viewModel = viewModel,
                onNavigateToEditPage = onNavigateToEditPage
            )
        }

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WeekPager(
    pagerState: PagerState,
    uiState: WeeklyUiState,
    viewModel: WeeklyViewModel,
    onNavigateToEditPage: (id: Int?, type: PlanType, date: Date?) -> Unit
) {
    CompositionLocalProvider(
        // ui 상에서 스와이프 시 나타나는 오버스크롤 효과 제거
        LocalOverscrollConfiguration provides null
    ) {
        HorizontalPager(
            modifier = Modifier.fillMaxSize(), state = pagerState, pageCount = uiState.pageCount
        ) { _ ->
            WeekScreen(
                modifier = Modifier.fillMaxWidth(),
                uiState = uiState,
                viewModel = viewModel,
                onNavigateToEditPage = onNavigateToEditPage
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WeekScreen(
    uiState: WeeklyUiState,
    viewModel: WeeklyViewModel,
    modifier: Modifier = Modifier,
    scheduleContent: @Composable (schedule: Schedule) -> Unit = { ScheduleItem(schedule = it, onNavigateToEditPage = onNavigateToEditPage) },
    todoContent: @Composable (todo: Todo) -> Unit = { TodoItem(viewModel = viewModel ,todo = it, onNavigateToEditPage = onNavigateToEditPage) },
    onNavigateToEditPage: (id: Int?, type: PlanType, date: Date?) -> Unit
) {
    val hourHeight = 40.dp
    val verticalScrollState = rememberScrollState()
    var sidebarWidth by remember { mutableStateOf(0.dp) }
    // table 시작 시간 설정
    val dpValue = hourHeight * 7
    val pixelValue = with(LocalDensity.current) { dpValue.toPx() }
    LaunchedEffect(Unit) {
        // 이미 계산된 픽셀 값으로 스크롤 이동
        verticalScrollState.scrollTo(pixelValue.roundToInt())
    }

    BoxWithConstraints(modifier = modifier) {
        val totalWidth = maxWidth
        val density = LocalDensity.current
        val dayWidth = (totalWidth - sidebarWidth) / 7
        Column {
            WeekHeader(
                uiState = uiState,
                onNavigateToEditPage = onNavigateToEditPage,
                dayWidth = dayWidth,
                modifier = Modifier
                    .padding(start = sidebarWidth)
            )
            Row(modifier = Modifier
                .weight(1f)
                .verticalScroll(verticalScrollState)) {
                WeekSidebar(hourHeight = hourHeight,
                            modifier = Modifier.onGloballyPositioned { layoutCoordinates ->
                                // Update sidebarWidth within the context of the composable
                                sidebarWidth = with(density) { layoutCoordinates.size.width.toDp() }
                            }
                )
                WeeklyTable(
                    uiState = uiState,
                    viewModel = viewModel,
                    dayWidth = dayWidth,
                    hourHeight = hourHeight,
                    scheduleContent = scheduleContent,
                    todoContent = todoContent,
                    onNavigateToEditPage = onNavigateToEditPage,
                    modifier = Modifier
                        .weight(1f)

                )
            }
        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Header(
    uiState: WeeklyUiState,
    coroutineScope: CoroutineScope,
    pagerState: PagerState,
    onPreviousWeekClick: () -> Unit,
    onNextWeekClick: () -> Unit
) {
    val typography = MaterialTheme.typography
    val displayText = formatWeekRange(uiState.currentWeek)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                if (pagerState.currentPage > 0) {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                    }
                    onPreviousWeekClick()
                }
            },
        ) {
            Icon(
                Icons.Filled.ArrowLeft, contentDescription = "Previous Week", tint = Color.Black
            )
        }
        Text(
            text = displayText,
            color = Color.Black,
            style = typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        IconButton(
            onClick = {
                if (pagerState.currentPage < uiState.pageCount - 1) {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                    onNextWeekClick()
                }
            },
        ) {
            Icon(
                Icons.Filled.ArrowRight, contentDescription = "Next Week", tint = Color.Black
            )
        }
    }
}

fun formatWeekRange(weekRange: Pair<Date, Date>): String {
    val currentYear = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())
    val dateFormatWithYear = SimpleDateFormat("yy년 MM월 dd일", Locale.getDefault())
    val dateFormatWithoutYear = SimpleDateFormat("MM월 dd일", Locale.getDefault())

    val startFormat =
        if (SimpleDateFormat("yyyy", Locale.getDefault()).format(weekRange.first)!=currentYear) {
            dateFormatWithYear
        } else {
            dateFormatWithoutYear
        }

    val endFormat =
        if (SimpleDateFormat("yyyy", Locale.getDefault()).format(weekRange.second)!=currentYear) {
            dateFormatWithYear
        } else {
            dateFormatWithoutYear
        }

    val start = startFormat.format(weekRange.first)
    val end = endFormat.format(weekRange.second)

    return "$start - $end"
}


