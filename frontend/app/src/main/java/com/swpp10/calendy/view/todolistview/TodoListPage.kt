package com.swpp10.calendy.view.todolistview

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowLeft
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.swpp10.calendy.AppViewModelProvider
import com.swpp10.calendy.data.maindb.plan.Todo
import com.swpp10.calendy.ui.theme.getColor
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToDoListPage(
    viewModel: TodoListViewModel = viewModel(factory = com.swpp10.calendy.AppViewModelProvider.Factory),
    onNavigateToEditPage: (Int?, Date?, Date?) -> Unit
) {
    val uiState: TodoListUiState by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }


    val onPreviousClick = {
        if (uiState.month==1) {
            viewModel.updateYear(uiState.year - 1)
            viewModel.updateMonth(12)
        } else {
            viewModel.updateMonth(uiState.month - 1)
        }
    }

    val onNextClick = {
        if (uiState.month==12) {
            viewModel.updateYear(uiState.year + 1)
            viewModel.updateMonth(1)
        } else {
            viewModel.updateMonth(uiState.month + 1)
        }
    }

    val onTextClick = { showBottomSheet = true }
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false }, sheetState = sheetState
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 450.dp)
            ) {
                MonthSelectionSheet(selectedYear = uiState.year,
                                    selectedMonth = uiState.month,
                                    onMonthYearSelected = { newYear, newMonth ->
                                        viewModel.updateYear(newYear)
                                        viewModel.updateMonth(newMonth)
                                        showBottomSheet = false
                                    },
                                    onDismiss = { showBottomSheet = false })
            }
        }

    }

    Scaffold(topBar = {
            Text(
                text = "TODO List",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 20.dp, bottom = 10.dp, start=20.dp,end=20.dp).fillMaxWidth(),
//                textAlign = TextAlign.Center
            )
          },
    floatingActionButton = {
        FloatingActionButton(
            onClick = {
                onNavigateToEditPage(null, Calendar.getInstance().apply {
                    set(Calendar.YEAR, uiState.year)
                    set(Calendar.MONTH, uiState.month - 1)
                }.time, null)
            },
            containerColor = Color(0xFF80ACFF),
            contentColor = Color.White,
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add")
        }
    })
    { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {

                MonthSelector(
                    modifier = Modifier.weight(1f),
                    year = uiState.year,
                    month = uiState.month,
                    onPreviousClick = onPreviousClick,
                    onNextClick = onNextClick,
                    onTextClick = onTextClick
                )
                hideToggle(viewModel, uiState)
            }
            OneMonthTodos(viewModel, uiState, onNavigateToEditPage)
        }
    }

}



@Composable
fun OneMonthTodos(
    viewModel: TodoListViewModel,
    uiState: TodoListUiState,
    onNavigateToEditPage: (Int?, Date?, Date?) -> Unit
) {
    viewModel.updateMonthTodos()
    Column {
        Divider()
        if (uiState.monthTodos.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "아직 TODO가 없습니다\nTODO를 추가해보세요!",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn {
                val filteredTodos = if (uiState.hidedStatus) {
                    uiState.monthTodos.filter { !it.complete }
                } else {
                    uiState.monthTodos
                }
                items(filteredTodos) { todo ->
                    ToDoItem(todo, viewModel, uiState, onNavigateToEditPage)
                }
            }
        }
    }
}


@Composable
fun ToDoItem(
    todo: Todo,
    viewModel: TodoListViewModel,
    uiState: TodoListUiState,
    onNavigateToEditPage: (Int?, Date?, Date?) -> Unit
) {
    var isChecked by remember(todo.complete) { mutableStateOf(todo.complete) }

    val clickAction = {
        onNavigateToEditPage(todo.id, null, null)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end =8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable(onClick = clickAction)
                .weight(1f)
        ) {
            Column(modifier = Modifier.padding(3.dp)) {
                Text(
                    text = todo.title,
                    textDecoration = if (isChecked) TextDecoration.LineThrough else null,
                    color = if (isChecked) Color.Gray else LocalContentColor.current,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = formatToHourMinuteAmPm(todo.dueTime),
                    style = MaterialTheme.typography.labelSmall,
                    textDecoration = if (isChecked) TextDecoration.LineThrough else null,
                    color = if (isChecked) Color.Gray else LocalContentColor.current
                )
            }
        }
        Checkbox(
             checked = todo.complete,
             onCheckedChange = {viewModel.updateCompletionOfTodo(todo, it)},
             colors = CheckboxDefaults.colors(
                 checkedColor = todo.getColor(),
                 uncheckedColor = todo.getColor(),
                 checkmarkColor = Color.White
             )
        )

    }
}

@Composable
fun MonthSelector(
    modifier: Modifier= Modifier,
    year: Int,
    month: Int,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onTextClick: () -> Unit
) {
    val typography = MaterialTheme.typography
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val displayText = if (year==currentYear) {
        "${month}월"
    } else {
        "${year}년 ${month}월"
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 5.dp, start = 4.dp, end = 16.dp, bottom = 5.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { onPreviousClick() },
        ) {
            Icon(
                Icons.Filled.ArrowLeft, contentDescription = "Previous Month", tint = Color.Black
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(text = displayText,
             color = Color.Black,
             style = MaterialTheme.typography.bodyLarge,
             textDecoration = TextDecoration.Underline,
             modifier = Modifier.clickable { onTextClick() })

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            onClick = { onNextClick() },
        ) {
            Icon(
                Icons.Filled.ArrowRight, contentDescription = "Next Month", tint = Color.Black
            )
        }
    }
}

@Composable
fun MonthSelectionSheet(
    selectedYear: Int,
    selectedMonth: Int,
    onMonthYearSelected: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val years = (min(selectedYear - 2, currentYear)..max(selectedYear + 2, currentYear)).toList()
    val months = (1..12).toList()
    // (year,list) 형태의 리스트로 변환
    val yearMonthCombinations = years.flatMap { year -> months.map { month -> year to month } }

    // LazyListState 생성
    val listState = rememberLazyListState()
    // 초기 스크롤 위치를 선택된 연도와 월에 기반하여 설정
    val initialIndex =
        yearMonthCombinations.indexOfFirst { it.first==selectedYear && it.second==selectedMonth }
    LaunchedEffect(key1 = initialIndex) {
        listState.scrollToItem(initialIndex)
    }


    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "월 선택하기",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            IconButton(onClick = { onDismiss() }) {
                Icon(
                    imageVector = Icons.Filled.Close, contentDescription = "Close"
                )
            }
        }

        LazyColumn(state = listState) {
            items(yearMonthCombinations) { (year, month) ->
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onMonthYearSelected(year, month) }
                    .padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${year}년 ${month}월",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (year==selectedYear && month==selectedMonth) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun hideToggle(viewModel: TodoListViewModel, uiState: TodoListUiState) {
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(end = 16.dp)
    ){
        Text(
            text = "완료된 TODO 숨기기",
            color = Color.Gray,
            style = MaterialTheme.typography.bodyMedium,
//            fontWeight = FontWeight.Bold,
            fontSize=12.sp
        )
        Spacer(modifier = Modifier.width(8.dp))
        Switch(checked = uiState.hidedStatus,
               colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF337AFF)),
               onCheckedChange = {
                   viewModel.setHidedStatus(it)
               },
               thumbContent = if (uiState.hidedStatus) {
                   {
                       Icon(
                           imageVector = Icons.Filled.Check,
                           contentDescription = null,
                           modifier = Modifier.size(SwitchDefaults.IconSize),
                       )
                   }
               } else {
                   null
               })
    }
}

fun formatToHourMinuteAmPm(date: Date): String {
    val formatter = SimpleDateFormat("d일(E) a hh:mm")
    return formatter.format(date)
}


