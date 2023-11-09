package com.example.calendy.view.todolistview

import androidx.compose.foundation.layout.*
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.calendy.data.dummy.DummyTodoRepository
import com.example.calendy.data.plan.Plan
import com.example.calendy.data.plan.Todo
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToDoListPage(
    todoListViewModel: TodoListViewModel,
    onNavigateToEditPage: (date: Date?) -> Unit
) {
    val currentState: TodoListUiState by todoListViewModel.uiState.collectAsState()
    val lazyListState = rememberLazyListState()

    val coroutineScope = rememberCoroutineScope()

    // 초기에 현재 날짜를 표시한 다음 미래 날짜를 로드
    val currentDate = remember { Calendar.getInstance().time }
    val todosForToday =
        todoListViewModel.getTodosForDate(currentDate).collectAsState(initial = emptyList()).value

    // snapshotFlow로 첫 번째 보이는 아이템 인덱스를 관찰
    val firstVisibleItemIndexFlow = snapshotFlow { lazyListState.firstVisibleItemIndex }
    val loadMorePast = remember { mutableStateOf(false) }
    val loadMoreFuture = remember { mutableStateOf(false) }
    // 과거 날짜의 데이터를 담을 상태 리스트
    val pastDatesWithTodos = remember { mutableStateListOf<Pair<Date, List<Todo>>>() }


    LaunchedEffect(firstVisibleItemIndexFlow) {
        firstVisibleItemIndexFlow.collect { firstVisibleItemIndex ->
            if (firstVisibleItemIndex==0) {
                // 상단에 스크롤됐을 때 과거 날짜를 로드하기 위한 flag 설정
                loadMorePast.value = true
                // 상단에 도달했을 때 과거 날짜를 추가
                if (loadMorePast.value) {
                    // 과거 날짜를 추가하는 함수
                    suspend fun loadPastTodos() {
                        // 현재 리스트의 첫 날짜를 가져옴
                        val currentStartDate =
                            pastDatesWithTodos.firstOrNull()?.first ?: currentDate

                        // 과거 날짜 데이터를 가져올 Calendar 객체 설정
                        val calendar = Calendar.getInstance()
                        calendar.time = currentStartDate
                        calendar.add(Calendar.DAY_OF_YEAR, -1)

                        // 과거 날짜의 데이터를 로드
                        val pastDate = calendar.time
                        val todos =
                            todoListViewModel.getTodosForDate(pastDate).first() // 첫 번째 데이터만 가져옴

                        // 상태 리스트의 상단에 과거 날짜와 할 일 리스트 추가
                        pastDatesWithTodos.add(0, Pair(pastDate, todos))

                        // LazyColumn의 스크롤 위치를 현재 위치로 재조정
                        coroutineScope.launch {
                            lazyListState.scrollToItem(index = 0)
                        }
                    }
                    // 과거 날짜 로드 함수 실행
                    coroutineScope.launch {
                        loadPastTodos()
                    }
                    // 플래그 리셋
                    loadMorePast.value = false
                }
            }
        }
    }

    Scaffold(topBar = {
//            TopAppBar(
//            title = { },
//            actions = {
//                // 오른쪽에 텍스트 버튼 추가
//                TextButton(onClick = { /* TODO: 완료된 할 일 숨기기 기능 구현 */ }) {
//                    Text(
//                        text = "Hide completed Todos",
//                        color = Color.Blue,
//                        fontSize = 12.sp
//                    )
//                }
//            }
//        )
    }, floatingActionButton = {
        FloatingActionButton(onClick = {
            onNavigateToEditPage(null)
        }) {
            Icon(Icons.Filled.Add, contentDescription = "Add")
        }
    }) { innerPadding ->
        LazyColumn(state = lazyListState, modifier = Modifier.padding(innerPadding)) {


            val calendar = Calendar.getInstance()
            val endRange = (calendar.clone() as Calendar).apply {
                add(Calendar.DAY_OF_YEAR, 30)
            }
            calendar.time = currentDate
            item { OneDayTodos(date = currentDate, todos = todosForToday, todoListViewModel = todoListViewModel) }
            calendar.add(Calendar.DAY_OF_YEAR, 1)

            while (calendar.before(endRange)) {
                val futureDate = calendar.time
                item {
                    val todos = todoListViewModel.getTodosForDate(futureDate)
                        .collectAsState(initial = emptyList()).value
                    OneDayTodos(date = futureDate, todos = todos, todoListViewModel = todoListViewModel)
                }
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }

        }
    }
}

@Composable
fun OneDayTodos(date: Date, todos: List<Todo>, todoListViewModel: TodoListViewModel) {
    Column {
        Divider()
        Text(
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp),
            text = if (isToday(date)) "Today"
            else if (isTomorrow(date)) "Tomorrow"
            else SimpleDateFormat("MM.dd", Locale.getDefault()).format(date)
        )
        todos.forEach { toDo ->
            ToDoItem(toDo, todoListViewModel)
        }
    }
}

@Composable
fun ToDoItem(todo: Todo, todoListViewModel: TodoListViewModel) {
    var isChecked by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = isChecked, onCheckedChange = {
            isChecked = it
            todoListViewModel.updateCompletionOfTodo(todo)
        })
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.padding(3.dp)){
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
}

fun isToday(date: Date): Boolean {
    val calendarNow = Calendar.getInstance()
    val calendarDate = Calendar.getInstance()
    calendarDate.time = date

    return calendarNow.get(Calendar.YEAR)==calendarDate.get(Calendar.YEAR) && calendarNow.get(
        Calendar.DAY_OF_YEAR
    )==calendarDate.get(Calendar.DAY_OF_YEAR)
}

fun isTomorrow(date: Date): Boolean {
    val calendarNow = Calendar.getInstance()
    val calendarDate = Calendar.getInstance()
    calendarDate.time = date
    calendarNow.add(Calendar.DAY_OF_YEAR, 1)

    return calendarNow.get(Calendar.YEAR)==calendarDate.get(Calendar.YEAR) && calendarNow.get(
        Calendar.DAY_OF_YEAR
    )==calendarDate.get(Calendar.DAY_OF_YEAR)
}

fun formatToHourMinuteAmPm(date: Date): String {
    val formatter = SimpleDateFormat("hh:mm a")
    return formatter.format(date)
}


