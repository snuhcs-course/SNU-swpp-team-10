package com.example.calendy.view.todolistview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowLeft
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Scaffold
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calendy.data.maindb.plan.Todo
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.example.calendy.data.plan.Todo
import com.example.calendy.view.popup.EditButton
import com.example.calendy.view.popup.PlanDetailPopup
import com.example.calendy.view.popup.PopupHeaderTitle
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToDoListPage(
    viewModel: TodoListViewModel, onNavigateToEditPage: (id: Int?, date: Date?) -> Unit
) {
    val uiState: TodoListUiState by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }


    val onPreviousClick = {
        if (uiState.month==1) {
            viewModel.updateYear(uiState.year-1)
            viewModel.updateMonth(12)
        } else { viewModel.updateMonth(uiState.month-1) }
    }

    val onNextClick = {
        if (uiState.month==12) {
            viewModel.updateYear(uiState.year+1)
            viewModel.updateMonth(1)
        } else { viewModel.updateMonth(uiState.month+1) }
    }

    val onTextClick = {showBottomSheet = true}
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {showBottomSheet = false},
            sheetState = sheetState
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 450.dp)
            ) {
                MonthSelectionSheet(
                    selectedYear = uiState.year,
                    selectedMonth = uiState.month,
                    onMonthYearSelected = { newYear, newMonth ->
                        viewModel.updateYear(newYear)
                        viewModel.updateMonth(newMonth)
                        showBottomSheet = false
                    },
                    onDismiss = { showBottomSheet = false }
                )
            }
        }

    }

    Scaffold(topBar = {
        MediumTopAppBar(title = {
            MonthSelector(
                year = uiState.year,
                month = uiState.month,
                onPreviousClick = onPreviousClick,
                onNextClick = onNextClick,
                onTextClick = onTextClick
            )
        }, actions = { hideToggle(viewModel, uiState) })
    }, floatingActionButton = {
        FloatingActionButton(onClick = {
            onNavigateToEditPage(null,Calendar.getInstance().apply {
                set(Calendar.YEAR, uiState.year)
                set(Calendar.MONTH, uiState.month - 1)
            }.time)
        }) {
            Icon(Icons.Filled.Add, contentDescription = "Add")
        }
    }) { innerPadding ->
        OneMonthTodos(Modifier.padding(innerPadding), viewModel, uiState, onNavigateToEditPage)
    }
}


@Composable
fun OneMonthTodos(
    modifier: Modifier,
    viewModel: TodoListViewModel,
    uiState: TodoListUiState,
    onNavigateToEditPage: (id: Int?,date: Date?) -> Unit
) {
    viewModel.updateMonthTodos()
    Column(modifier) {
        Divider()
        if(uiState.monthTodos.isEmpty()){
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("아직 TODO가 없습니다\nTODO를 추가해보세요!")
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
fun ToDoItem(todo: Todo, viewModel: TodoListViewModel, uiState: TodoListUiState, onNavigateToEditPage: (id: Int?, date: Date?) -> Unit) {
    var isChecked by remember(todo.complete) { mutableStateOf(todo.complete) }
    var openDetailPopup by remember { mutableStateOf(false) }
    if(openDetailPopup) {
        PlanDetailPopup(
            plan = todo,
            header = { PopupHeaderTitle(todo.title)},
            editButton = {
                EditButton(
                    plan = todo,
                    onNavigateToEditPage = {_,_,_ -> onNavigateToEditPage(todo.id, null)},
                    modifier=Modifier.align(Alignment.TopEnd)
                )
            },
            onDismissed = { openDetailPopup = false })
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically){
            Checkbox(checked = todo.complete, onCheckedChange = {
                viewModel.updateCompletionOfTodo(todo, it)
            })
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.padding(3.dp)) {
                Text(
                    text = todo.title,
                    textDecoration = if (isChecked) TextDecoration.LineThrough else null,
                    color = if (isChecked) Color.Gray else LocalContentColor.current
                )
                Text(
                    text = formatToHourMinuteAmPm(todo.dueTime),
                    fontSize = 12.sp,
                    textDecoration = if (isChecked) TextDecoration.LineThrough else null,
                    color = if (isChecked) Color.Gray else LocalContentColor.current
                )
            }
        }
        IconButton(onClick = {openDetailPopup = true})
        {
            Icon(
                imageVector = Icons.Filled.MoreHoriz,
                contentDescription = "view detail"
            )
        }

    }
}

@Composable
fun MonthSelector(
    year: Int, month: Int, onPreviousClick: () -> Unit, onNextClick: () -> Unit, onTextClick: () -> Unit
) {
    val typography = MaterialTheme.typography
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val displayText = if (year == currentYear) {
        "${month}월 TODO"
    } else {
        "${year}년 ${month}월 TODO"
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 25.dp, start = 4.dp, end = 16.dp, bottom = 5.dp),
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
             style = typography.bodyMedium,
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
    val years = (currentYear - 2..currentYear + 2).toList()
    val months = (1..12).toList()
    // (year,list) 형태의 리스트로 변환
    val yearMonthCombinations = years.flatMap { year -> months.map { month -> year to month } }

    // LazyListState 생성
    val listState = rememberLazyListState()
    // 초기 스크롤 위치를 선택된 연도와 월에 기반하여 설정
    val initialIndex = yearMonthCombinations.indexOfFirst { it.first == selectedYear && it.second == selectedMonth }
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
                fontWeight = FontWeight.Bold ,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            IconButton(onClick = { onDismiss() }) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close"
                )
            }
        }

        LazyColumn(state = listState) {
            items(yearMonthCombinations) { (year, month) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onMonthYearSelected(year, month) }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${year}년 ${month}월",
                        modifier = Modifier.weight(1f)
                    )
                    if (year == selectedYear && month == selectedMonth) {
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
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "Hide Completed", color = Color.Blue, fontSize = 12.sp
        )
        Spacer(modifier = Modifier.width(8.dp))
        Switch(checked = uiState.hidedStatus, onCheckedChange = {
            viewModel.setHidedStatus(it)
        }, thumbContent = if (uiState.hidedStatus) {
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
    val formatter = SimpleDateFormat("d일 a hh:mm")
    return formatter.format(date)
}


