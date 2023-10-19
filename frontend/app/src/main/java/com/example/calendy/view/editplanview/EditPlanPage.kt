package com.example.calendy.view.editplanview

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calendy.data.AppViewModelProvider
import com.example.calendy.data.DummyScheduleRepository
import com.example.calendy.data.DummyTodoRepository
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarConfig
import com.sd.lib.compose.wheel_picker.FVerticalWheelPicker
import com.sd.lib.compose.wheel_picker.rememberFWheelPickerState
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPlanPage(editPlanViewModel: EditPlanViewModel = viewModel(factory = AppViewModelProvider.Factory)) {
    val editPlanUiState by editPlanViewModel.uiState.collectAsState()
    val verticalScrollState = rememberScrollState(initial = 0)
    Column(
            modifier = Modifier
                    .verticalScroll(verticalScrollState)
                    .fillMaxSize()
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Top Bar
        TopAppBar()

        //region Type Buttons
        Row(
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf(EntryType.Schedule, EntryType.Todo).forEach {
                TypeButton(text = it.text,
                        isSelected = (editPlanUiState.entryType == it),
                        onClick = {
                            editPlanViewModel.setType(it)
                        })
            }
        }
        //endregion

        //region Title Text Field
        TextField(value = editPlanUiState.titleField,
                placeholder = {
                    Text(
                            "제목", style = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    )
                },
                onValueChange = { value -> editPlanViewModel.setTitle(value) },
                colors = Color.Transparent.let {
                    TextFieldDefaults.colors(
                            focusedContainerColor = it,
                            unfocusedContainerColor = it,
                            disabledContainerColor = it,
                            errorContainerColor = it,
                            focusedIndicatorColor = Color.Black,
                            unfocusedIndicatorColor = Color.Black,
                            disabledIndicatorColor = Color.Black,
                            errorIndicatorColor = Color.Black
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold)
        )
        //endregion

        // Date Selector
        DateSelector(modifier = Modifier.align(alignment = Alignment.CenterHorizontally))

        // Repeat
        TextField(value = "반복안함", onValueChange = { /* TODO: Handle text input */ })

        // Category
        TextField(value = "category", onValueChange = { /* TODO: Handle text input */ })

        // Priority
        RatingBar(
                value = editPlanUiState.priority.toFloat(),
                onValueChange = { editPlanViewModel.setPriority(it.toInt()) },
                onRatingChanged = { },
                config = RatingBarConfig().size(40.dp)
        )

        //region Memo Text Field
        TextField(value = editPlanUiState.memoField,
                placeholder = { Text("메모") },
                onValueChange = { value -> editPlanViewModel.setMemo(value) },
                colors = Color.Transparent.let {
                    TextFieldDefaults.colors(
                            focusedContainerColor = it,
                            unfocusedContainerColor = it,
                            disabledContainerColor = it,
                            errorContainerColor = it,
                            focusedIndicatorColor = Color.Black,
                            unfocusedIndicatorColor = Color.Black,
                            disabledIndicatorColor = Color.Black,
                            errorIndicatorColor = Color.Black
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontSize = 20.sp)
        )
        //endregion

        // Submit Button
        Button(onClick = { /* TODO: Handle on submit */ }) {
            Text(text = "Done")
        }
    }
}

@Composable
fun TypeButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
            onClick = { onClick() },
            colors = ButtonDefaults.buttonColors(if (isSelected) Color(0xFF7986CB) else Color.Gray),
            border = BorderStroke(2.dp, if (isSelected) Color.Black else Color.Black),
            shape = RoundedCornerShape(20),
            modifier = Modifier
                    .width(136.dp)
                    .height(36.dp)

    ) {
        Text(text = text, fontSize = 16.sp)
    }
}

@Composable
fun TopAppBar() {
    Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateSelector(modifier: Modifier = Modifier) {
    var selectedIndex by remember { mutableStateOf(-1) }
    val options = listOf("Yearly", "Monthly", "Daily")
    val isYearly = selectedIndex == 0
    val isMonthly = selectedIndex == 1
    val isDaily = selectedIndex == 2

    val calendar = Calendar.getInstance()
    var isDialogOpen by remember { mutableStateOf(false) }
    var isTimePickerOpen by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = calendar.timeInMillis)
    val dateRangePickerState = rememberDateRangePickerState()
    val timePickerState = rememberTimePickerState(0, 0, false)

    val yearPickerState = rememberFWheelPickerState(calendar.get(Calendar.YEAR) - 2001)
    val monthPickerState = rememberFWheelPickerState(calendar.get(Calendar.MONTH))

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        val shape = RoundedCornerShape(8.dp)
        Row(
                modifier = Modifier
                        .height(32.dp)
                        .clip(shape = shape)
                        .border(width = 1.dp, color = Color.Black, shape = shape)
        ) {
            options.forEachIndexed { index, label ->
                Button(
                        onClick = { selectedIndex = if (selectedIndex == index) -1 else index },
                        colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedIndex == index) Color(0xFF7986CB) else Color.Transparent,
                                contentColor = Color.Black
                        ),
                        shape = RectangleShape
                ) {
                    Text(text = label)
                }
            }
        }

        Box(modifier = Modifier.requiredHeight(60.dp)) {
            if (isYearly) {
                FVerticalWheelPicker(
                        modifier = Modifier
                                .width(60.dp)
                                .align(Alignment.BottomCenter),
                        itemHeight = 20.dp,
                        count = 100,
                        state = yearPickerState
                ) { index ->
                    Text((index + 2001).toString())
                }
            } else if (isMonthly) {
                Row(
                        modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    FVerticalWheelPicker(
                            modifier = Modifier.width(60.dp),
                            itemHeight = 20.dp,
                            count = 100,
                            state = yearPickerState
                    ) { index ->
                        Text((index + 2001).toString())
                    }
                    FVerticalWheelPicker(
                            modifier = Modifier.width(60.dp),
                            itemHeight = 20.dp,
                            count = 12,
                            state = monthPickerState
                    ) { index ->
                        Text((index + 1).toString())
                    }
                }
            } else if (isDaily) {
                Button(
                        onClick = { isDialogOpen = true },
                        modifier = Modifier.align(Alignment.BottomCenter),
                        colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent, contentColor = Color.Black
                        ),
                ) {
                    val formatter = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
                    val textString = datePickerState.selectedDateMillis?.let {
                        calendar.timeInMillis = it
                        formatter.format(calendar.time)
                    } ?: "Not Selected"
                    Text(text = textString, style = TextStyle(fontSize = 20.sp))
                }

                DateTimePickerDialog(datePickerState = datePickerState,
                        dateRangePickerState = dateRangePickerState,
                        timePickerState = timePickerState,
                        isDialogOpen = isDialogOpen,
                        isTimePickerOpen = isTimePickerOpen,
                        onDismissRequest = {
                            isDialogOpen = false
                            isTimePickerOpen = false
                        },
                        onConfirmDate = {
                            isDialogOpen = false
                        },
                        onConfirmTime = { })
            } else {
                Button(
                        onClick = { isDialogOpen = true },
                        modifier = Modifier.align(Alignment.BottomCenter),
                        colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent, contentColor = Color.Black
                        ),
                ) {
                    val formatter = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
                    val textString = datePickerState.selectedDateMillis?.let {
                        calendar.timeInMillis = it
                        calendar.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                        calendar.set(Calendar.MINUTE, timePickerState.minute)
                        formatter.format(calendar.time)
                    } ?: "Not Selected"
                    Text(text = textString, style = TextStyle(fontSize = 20.sp))
                }

                DateTimePickerDialog(datePickerState = datePickerState,
                        dateRangePickerState = dateRangePickerState,
                        timePickerState = timePickerState,
                        isDialogOpen = isDialogOpen,
                        isTimePickerOpen = isTimePickerOpen,
                        onDismissRequest = {
                            isDialogOpen = false
                            isTimePickerOpen = false
                        },
                        onConfirmDate = {
                            isTimePickerOpen = true
                        },
                        onConfirmTime = {
                            isDialogOpen = false
                            isTimePickerOpen = false
                        })
            }
        }
    }
}


@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DateTimePickerDialog(
        datePickerState: DatePickerState,
        dateRangePickerState: DateRangePickerState,
        timePickerState: TimePickerState,
        isDialogOpen: Boolean,
        isTimePickerOpen: Boolean,
        onDismissRequest: () -> Unit,
        onConfirmDate: () -> Unit,
        onConfirmTime: () -> Unit
) {
    if (isDialogOpen && !isTimePickerOpen) {
        DatePickerDialog(onDismissRequest = onDismissRequest, dismissButton = {}, confirmButton = {
            IconButton(
                    onClick = { onConfirmDate() }, modifier = Modifier.padding(end = 16.dp)
            ) {
                Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Confirm",
                        modifier = Modifier.size(40.dp)
                )
            }
        }) {
            DatePicker(
                    state = datePickerState, showModeToggle = false
            )
        }
    } else if (isDialogOpen && isTimePickerOpen) {
        Dialog(
                onDismissRequest = { onDismissRequest() },
                properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                    modifier = Modifier
                            .width(320.dp)
                            .height(480.dp),
                    shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TimePicker(
                            state = timePickerState
                    )
                    IconButton(
                            onClick = { onConfirmTime() },
                            modifier = Modifier
                                    .align(Alignment.End)
                                    .padding(end = 16.dp)
                    ) {
                        Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "Confirm",
                                modifier = Modifier.size(40.dp)
                        )
                    }
                }

            }
        }
    }
}

@Preview(showBackground = true, name = "Todo Screen Preview")
@Composable
fun TodoScreenPreview() {
    EditPlanPage(
            editPlanViewModel = EditPlanViewModel(
                    scheduleRepository = DummyScheduleRepository(),
                    todoRepository = DummyTodoRepository()
            )
    )
}

