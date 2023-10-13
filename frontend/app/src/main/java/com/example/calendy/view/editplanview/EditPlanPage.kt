package com.example.calendy.view.editplanview

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPlanPage(editPlanViewModel: EditPlanViewModel = viewModel()) {
    val editPlanUiState by editPlanViewModel.uiState.collectAsState()
    var selectedButton by remember { mutableStateOf("일정") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top Bar
        TopAppBar()

        // Type Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TypeButton(text = "일정", isSelected = false)
            TypeButton(text = "TODO", isSelected = true)
        }

        Spacer(modifier = Modifier.height(32.dp))

        TextField(
            value = "Todo 제목",
            onValueChange = { /* TODO: Handle text input */ },
            colors = Color.Transparent.let {
                TextFieldDefaults.colors(
                    focusedContainerColor = it,
                    unfocusedContainerColor = it,
                    disabledContainerColor = it,
                    errorContainerColor = it,
                    focusedIndicatorColor = it,
                    unfocusedIndicatorColor = it,
                    disabledIndicatorColor = it,
                    errorIndicatorColor = it
                )
            },
            textStyle = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Date Selector
        DateSelector(modifier = Modifier.align(alignment = Alignment.CenterHorizontally))

        TextField(value = "반복안함", onValueChange = { /* TODO: Handle text input */ })

        Spacer(modifier = Modifier.height(20.dp))

        TextField(value = "category", onValueChange = { /* TODO: Handle text input */ })

        Spacer(modifier = Modifier.height(20.dp))

        // Category
        StarRating()

        TextField(value = "메모", onValueChange = { /* TODO: Handle text input */ })

        Spacer(modifier = Modifier.height(20.dp))

        // Submit Button
        Button(onClick = { /* TODO: Handle on submit */ }) {
            Text(text = "Done")
        }
    }
}

@Composable
fun TypeButton(text: String, isSelected: Boolean) {
    Button(
        onClick = { /* todo */ },
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
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateSelector(modifier: Modifier = Modifier) {
    var selectedIndex by remember { mutableStateOf(-1) }
    val options = listOf("Yearly", "Monthly", "Daily")

    val calendar = Calendar.getInstance()
    var isDialogOpen by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = calendar.timeInMillis)
    val dateRangePickerState = rememberDateRangePickerState()
    val timePickerState = rememberTimePickerState(0, 0, false)

    DateTimePickerDialog(
        datePickerState = datePickerState,
        dateRangePickerState = dateRangePickerState,
        timePickerState = timePickerState,
        isDialogOpen = isDialogOpen
    ) { isDialogOpen = false }

    Column(modifier = modifier) {
        Row(modifier = Modifier.align(alignment = Alignment.CenterHorizontally)) {
            options.forEachIndexed { index, label ->
                Button(
                    onClick = { selectedIndex = if (selectedIndex == index) -1 else index },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedIndex == index) Color(0xFF7986CB) else Color.Transparent,
                        contentColor = Color.Black
                    )
                ) {
                    Text(text = label)
                }
            }
        }
        Button(onClick = { isDialogOpen = true }) {
            Text(text = "2023년 10월 19일 (목) 오전 9:00")
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
    onDismissRequest: () -> Unit,
) {
    if (isDialogOpen) {
        DatePickerDialog(
            onDismissRequest = onDismissRequest,
            dismissButton = {
                Button(onClick = { onDismissRequest() }) {
                    Text(text = "Cancel")
                }
            },
            confirmButton = {
                Button(onClick = { onDismissRequest() }) {
                    Text(text = "Confirm")
                }
            }) {
            DateRangePicker(state = dateRangePickerState)
//            DatePicker(
//                state = datePickerState,
//                showModeToggle = false
//            )
//            TimePicker(state = timePickerState, layoutType = TimePickerLayoutType.Horizontal)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(widthDp = 600)
@Composable
fun DateTimePickerDialogPreview() {
    val calendar = Calendar.getInstance()

    var isDialogOpen by remember { mutableStateOf(true) }
    val datePickerState =
        rememberDatePickerState(initialSelectedDateMillis = calendar.timeInMillis)
    val dateRangePickerState = rememberDateRangePickerState()
    val timePickerState = rememberTimePickerState()

    DateTimePickerDialog(
        datePickerState = datePickerState,
        dateRangePickerState = dateRangePickerState,
        timePickerState = timePickerState,
        isDialogOpen = isDialogOpen
    ) { isDialogOpen = false }
}

@Composable
fun StarRating() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        // You can use 'Icon' for each star and change its tint based on selection
        // Example for one star
        Icon(imageVector = Icons.Default.Star, contentDescription = "Star", tint = Color.Yellow)
        Icon(imageVector = Icons.Default.Star, contentDescription = "Star", tint = Color.Yellow)
        Icon(imageVector = Icons.Default.Star, contentDescription = "Star", tint = Color.Yellow)
        Icon(imageVector = Icons.Default.Star, contentDescription = "Star", tint = Color.Yellow)
        Icon(imageVector = Icons.Default.Star, contentDescription = "Star", tint = Color.Yellow)
    }
}

@Preview(showBackground = true, name = "Todo Screen Preview")
@Composable
fun TodoScreenPreview() {
    EditPlanPage()
}

