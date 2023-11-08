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
import com.example.calendy.data.dummy.DummyTodoRepository
import com.example.calendy.data.plan.Plan
import com.example.calendy.data.plan.Todo
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun ToDoListPage(todoListViewModel: TodoListViewModel, onNavigateToEditPage: (date: Date?) -> Unit) {
    val currentState: TodoListUiState by todoListViewModel.uiState.collectAsState()
    val lazyListState = rememberLazyListState()

    val coroutineScope = rememberCoroutineScope()

    // 초기에 현재 날짜를 표시한 다음 미래 날짜를 로드
    val currentDate = remember { Calendar.getInstance().time }
    val todosForToday = todoListViewModel.getTodosForDate(currentDate).collectAsState(initial = emptyList()).value

    // snapshotFlow로 첫 번째 보이는 아이템 인덱스를 관찰
    val firstVisibleItemIndexFlow = snapshotFlow { lazyListState.firstVisibleItemIndex }
    val loadMorePast = remember { mutableStateOf(false) }
    val loadMoreFuture = remember { mutableStateOf(false)}

    LaunchedEffect(firstVisibleItemIndexFlow) {
        firstVisibleItemIndexFlow.collect { firstVisibleItemIndex ->
            if (firstVisibleItemIndex == 0) {
                // 상단에 스크롤됐을 때 과거 날짜를 로드하기 위한 flag 설정
                loadMorePast.value = true
            }
        }
    }

    Scaffold(
        topBar = {},
        floatingActionButton = {
            FloatingActionButton(onClick = {
                onNavigateToEditPage(null)
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
            }
        }
    ) { innerPadding ->
        LazyColumn(state = lazyListState, modifier = Modifier.padding(innerPadding)) {

            val calendar = Calendar.getInstance()
            val endRange = (calendar.clone() as Calendar).apply {
                add(Calendar.DAY_OF_YEAR, 10)
            }
            calendar.time = currentDate
            item { OneDayTodos(date = currentDate, todos = todosForToday) }
            calendar.add(Calendar.DAY_OF_YEAR, 1)

            while (calendar.before(endRange)) {
                val futureDate = calendar.time
                item {
                    val todos = todoListViewModel.getTodosForDate(futureDate).collectAsState(initial = emptyList()).value
                    OneDayTodos(date = futureDate, todos = todos)
                }
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }

        }
    }
}

@Composable
fun OneDayTodos(date: Date, todos: List<Todo>) {
    Column {
        Divider()
        Text(
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp),
            text = SimpleDateFormat("MM.dd", Locale.getDefault()).format(date)
        )
        todos.forEach { toDo ->
            ToDoItem(toDo)
        }
    }
}
@Composable
fun ToDoItem(todo: Todo) {
    var isChecked by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = { isChecked = it }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = todo.title,
            textDecoration = if (isChecked) TextDecoration.LineThrough else null,
            color = if (isChecked) Color.Gray else LocalContentColor.current
        )
    }
}


