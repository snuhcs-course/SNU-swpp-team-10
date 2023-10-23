package com.example.calendy.view.editplanview

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calendy.AppViewModelProvider
import com.example.calendy.data.dummy.DummyCategoryRepository
import com.example.calendy.data.dummy.DummyScheduleRepository
import com.example.calendy.data.dummy.DummyTodoRepository
import com.example.calendy.data.category.Category
import com.example.calendy.data.dummy.DummyRepeatGroupRepository
import com.example.calendy.data.plan.Plan.PlanType
import com.example.calendy.utils.DateHelper.extract
import com.example.calendy.utils.DateHelper.getDateInMillis
import com.example.calendy.utils.bottomBorder
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarConfig
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialog
import com.sd.lib.compose.wheel_picker.FVerticalWheelPicker
import com.sd.lib.compose.wheel_picker.FWheelPickerState
import com.sd.lib.compose.wheel_picker.rememberFWheelPickerState
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun EditPlanPage(editPlanViewModel: EditPlanViewModel = viewModel(factory = AppViewModelProvider.Factory)) {
    val editPlanUiState: EditPlanUiState by editPlanViewModel.uiState.collectAsState()
    val categoryList: List<Category> by editPlanViewModel.categoryListState.collectAsState()

    val verticalScrollState = rememberScrollState(initial = 0)
    Column(
        modifier = Modifier
            .verticalScroll(verticalScrollState)
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        //region Top Bar
        TopAppBar(showBackButton = true,
                  onBackPressed = { /* Todo */ },
                  title = { },
                  trailingContent = {
                      Row {
                          // Delete Button
                          IconButton(onClick = { /*TODO Delete Button */ }) {
                              Icon(
                                  imageVector = Icons.Default.Delete, contentDescription = "Delete"
                              )
                          }
                          // Save Button
                          IconButton(onClick = { /*TODO Save Button */ }) {
                              Icon(imageVector = Icons.Default.Save, contentDescription = "Submit")
                          }
                      }
                  })
        //endregion

        //region Type Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf(PlanType.Schedule, PlanType.Todo).forEach {
                TypeButton(text = when (it) {
                    PlanType.Schedule -> "일정"
                    PlanType.Todo     -> "Todo"
                }, isSelected = (editPlanUiState.entryType==it), onClick = {
                    editPlanViewModel.setType(it)
                })
            }
        }
        //endregion

        //region Title Text Field & Checkbox
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(value = editPlanUiState.titleField,
                      placeholder = {
                          Text(
                              "제목",
                              style = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold)
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
                      modifier = Modifier.weight(1f),
                      textStyle = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold)
            )
            if(editPlanUiState.entryType == PlanType.Todo){
                Checkbox(
                    checked = editPlanUiState.isComplete,
                    onCheckedChange = { editPlanViewModel.setIsComplete(it) },
                    modifier = Modifier.scale(1.5f)
                )
            }
        }
        //endregion

        //region Date Selector
        if(editPlanUiState.entryType == PlanType.Todo) {
            DateSelector(
                dueTime = editPlanUiState.dueTime,
                onSelectDueTime = editPlanViewModel::setDueTime,
                onSelectDueYear = editPlanViewModel::setDueYear,
                onSelectDueMonth = editPlanViewModel::setDueMonth,
                isYearly = editPlanUiState.isYearly,
                toggleIsYearly = editPlanViewModel::toggleIsYearly,
                isMonthly = editPlanUiState.isMonthly,
                toggleIsMonthly = editPlanViewModel::toggleIsMonthly,
                isDaily = editPlanUiState.isDaily,
                toggleIsDaily = editPlanViewModel::toggleIsDaily,
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            )
        }

        //endregion

        //region Repeat
        SetRepeat(editPlanUiState)
        //endregion

        //region Category
        FieldWithLeadingText(leadingText = "분류") {
            Category(
                currentCategory = editPlanUiState.category,
                categoryList = categoryList,
                onAddCategory = editPlanViewModel::addCategory,
                onSelectCategory = editPlanViewModel::setCategory
            )
        }
        //endregion

        //region Priority
        FieldWithLeadingText(leadingText = "중요도") {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                RatingBar(
                    value = editPlanUiState.priority.toFloat(),
                    onValueChange = { editPlanViewModel.setPriority(it.toInt()) },
                    onRatingChanged = { },
                    config = RatingBarConfig().size(40.dp)
                )
            }
        }
        //endregion

        //region Memo Text Field
        FieldWithLeadingText(leadingText = "메모", alignment = Alignment.Top) {
            BasicTextField(value = editPlanUiState.memoField,
                           onValueChange = { value -> editPlanViewModel.setMemo(value) },
                           modifier = Modifier.fillMaxWidth(),
                           textStyle = TextStyle(fontSize = 16.sp),
                           decorationBox = { innerTextField ->
                               Column(
                                   modifier = Modifier.drawWithContent {
                                       drawContent()
                                       drawLine(
                                           color = Color(0xFF494949),
                                           start = Offset(
                                               x = 0f,
                                               y = size.height - 1.dp.toPx(),
                                           ),
                                           end = Offset(
                                               x = size.width,
                                               y = size.height - 1.dp.toPx(),
                                           ),
                                           strokeWidth = 1.dp.toPx(),
                                       )
                                   },
                               ) {
                                   Row(
                                       verticalAlignment = Alignment.CenterVertically,
                                       horizontalArrangement = Arrangement.spacedBy(12.dp),
                                   ) {
                                       innerTextField()
                                   }
                                   Spacer(modifier = Modifier.height(8.dp))
                               }
                           })
        }
        //endregion

        //region Show In Monthly View Switch
        FieldWithLeadingText(
            leadingText = "월별 보기에 표시", modifier = Modifier.fillMaxWidth(), textWidth = 240.dp
        ) {
            Switch(checked = editPlanUiState.showInMonthlyView,
                   onCheckedChange = { editPlanViewModel.setShowInMonthlyView(it) })
        }
        //endregion

    }
}

// TODO: AppBar can not stay on top when scroll. Maybe needs scaffold.
@Composable
fun TopAppBar(
    showBackButton: Boolean,
    onBackPressed: () -> Unit,
    title: @Composable RowScope.() -> Unit,
    trailingContent: @Composable RowScope.() -> Unit = { }
) {
    Row(
        modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
    ) {
        if (showBackButton) {
            IconButton(onClick = { onBackPressed() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIos,
                    contentDescription = "Back to previous screen"
                )
            }
        }
        title()
        Spacer(modifier = Modifier.weight(1f))
        trailingContent()
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

// Description leading text on left
@Composable
fun FieldWithLeadingText(
    leadingText: String,
    alignment: Alignment.Vertical = Alignment.CenterVertically,
    modifier: Modifier = Modifier,
    textWidth: Dp = 80.dp,
    content: @Composable() (RowScope.() -> Unit)
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = alignment,
        modifier = modifier
    ) {
        Text(
            text = leadingText,
            modifier = Modifier
                .width(textWidth)
                .padding(end = 16.dp),
            color = Color.Gray
        )
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateSelector(
    dueTime: Date,
    onSelectDueTime: (Date) -> Unit,
    onSelectDueYear: (Int) -> Unit,
    onSelectDueMonth: (Int, Int) -> Unit,
    isYearly: Boolean,
    toggleIsYearly: () -> Unit,
    isMonthly: Boolean,
    toggleIsMonthly: () -> Unit,
    isDaily: Boolean,
    toggleIsDaily: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isDialogOpen by remember { mutableStateOf(false) }
    fun openDialog() {
        isDialogOpen = true
    }

    fun closeDialog() {
        isDialogOpen = false
    }

    // TODO: refactor me (especially in Date Picker, Date Time Picker region)
    // TODO: Calendar 대신 uiState 날짜 사용
    val calendar = Calendar.getInstance()
    val dateRangePickerState = rememberDateRangePickerState()

    val (year, monthZeroIndexed, day, hour, minute) = dueTime.extract()
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dueTime.time)
    // dueTime Change -> date state is set
    datePickerState.setSelection(dueTime.time)
    val hourPickerState = rememberFWheelPickerState(initialIndex = hour)
    val minutePickerState = rememberFWheelPickerState(initialIndex = minute)


    val yearPickerState: FWheelPickerState = rememberFWheelPickerState(initialIndex = year - 2001)
    val monthPickerState = rememberFWheelPickerState(initialIndex = monthZeroIndexed)
    // dueTime Change -> year & month (handled in if isYearly, isMonthly statement below)
    // On year picker & month picker change -> dueTime Change
    LaunchedEffect(key1 = yearPickerState.currentIndex) {
        onSelectDueYear(yearPickerState.currentIndex + 2001)
    }
    LaunchedEffect(key1 = monthPickerState.currentIndex) {
        onSelectDueMonth(yearPickerState.currentIndex + 2001, monthPickerState.currentIndex)
    }


    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        //region 3 Buttons
        val shape = RoundedCornerShape(8.dp)
        Row(
            modifier = Modifier
                .height(32.dp)
                .clip(shape = shape)
                .border(width = 1.dp, color = Color.Black, shape = shape)
        ) {
            listOf(
                Triple("Yearly", isYearly, toggleIsYearly),
                Triple("Monthly", isMonthly, toggleIsMonthly),
                Triple("Daily", isDaily, toggleIsDaily),
            ).forEach { (label, isSelected, onClick) ->
                Button(
                    onClick = { onClick() }, colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) Color(0xFF7986CB) else Color.Transparent,
                        contentColor = Color.Black
                    ), shape = RectangleShape
                ) {
                    Text(text = label)
                }
            }
        }
        //endregion

        Box(modifier = Modifier.requiredHeight(60.dp)) {
            //region isYearly -> Pick Year
            if (isYearly) {
                // ViewModel Change -> Year Picker Updated
                LaunchedEffect(key1 = year) {
                    yearPickerState.scrollToIndex(year - 2001)
                }
                YearPicker(
                    state = yearPickerState, modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
            //endregion
            //region isMonthly -> Pick Year & Month
            else if (isMonthly) {
                // ViewModel Change -> Year Picker Updated
                LaunchedEffect(key1 = year) {
                    yearPickerState.scrollToIndex(year - 2001)
                }
                // ViewModel Change -> Month Picker Updated
                LaunchedEffect(key1 = monthZeroIndexed) {
                    monthPickerState.scrollToIndex(monthZeroIndexed)
                }

                Row(
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    YearPicker(state = yearPickerState)
                    MonthPicker(state = monthPickerState)
                }
            }
            //endregion
            //region isDaily -> Date Picker
            else if (isDaily) {
                Button(
                    onClick = { openDialog() },
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

                if (isDialogOpen) {
                    DateTimePickerDialog(
                        datePickerState = datePickerState,
                        dateRangePickerState = dateRangePickerState,
                        showTimePicker = false,
                        hourPickerState = hourPickerState,
                        minutePickerState = minutePickerState,
                        onDismissRequest = {
                            closeDialog()
                        },
                    ) { datePickerState, _, _ ->
                        closeDialog()
                        onSelectDueTime(
                            (datePickerState.selectedDateMillis ?: calendar.timeInMillis).let {
                                calendar.timeInMillis = it
                                calendar.set(Calendar.HOUR_OF_DAY, hour) // 기존 uiState 의 value 사용
                                calendar.set(Calendar.MINUTE, minute)
                                calendar.time
                            },
                        )
                    }
                }
            }
            //endregion
            //region Date & Time Picker
            else {
                Button(
                    onClick = { openDialog() },
                    modifier = Modifier.align(Alignment.BottomCenter),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent, contentColor = Color.Black
                    ),
                ) {
                    val formatter = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
                    val textString = datePickerState.selectedDateMillis?.let {
                        calendar.timeInMillis = it
                        calendar.set(Calendar.HOUR_OF_DAY, hourPickerState.currentIndex)
                        calendar.set(Calendar.MINUTE, minutePickerState.currentIndex)
                        formatter.format(calendar.time)
                    } ?: "Not Selected"
                    Text(text = textString, style = TextStyle(fontSize = 20.sp))
                }

                if (isDialogOpen) {
                    DateTimePickerDialog(
                        datePickerState = datePickerState,
                        dateRangePickerState = dateRangePickerState,
                        showTimePicker = true,
                        hourPickerState = hourPickerState,
                        minutePickerState = minutePickerState,
                        onDismissRequest = {
                            closeDialog()
                        },
                    ) { datePickerState, hour, minute ->
                        closeDialog()
                        onSelectDueTime(
                            (datePickerState.selectedDateMillis ?: calendar.timeInMillis).let {
                                calendar.timeInMillis = it
                                calendar.set(Calendar.HOUR_OF_DAY, hour)
                                calendar.set(Calendar.MINUTE, minute)
                                calendar.time
                            },
                        )
                    }
                }
            }
            //endregion
        }
    }
}

@Composable
fun YearPicker(state: FWheelPickerState, modifier: Modifier = Modifier) {
    FVerticalWheelPicker(
        modifier = modifier.width(60.dp), itemHeight = 20.dp, count = 100, state = state
    ) { index ->
        Text((index + 2001).toString())
    }
}

@Composable
private fun MonthPicker(state: FWheelPickerState, modifier: Modifier = Modifier) {
    FVerticalWheelPicker(
        modifier = modifier.width(60.dp), itemHeight = 20.dp, count = 12, state = state
    ) { index ->
        Text((index + 1).toString())
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DateTimePickerDialog(
    datePickerState: DatePickerState,
    dateRangePickerState: DateRangePickerState,
    showTimePicker: Boolean,
    hourPickerState: FWheelPickerState,
    minutePickerState: FWheelPickerState,
    onDismissRequest: () -> Unit,
    onConfirm: (DatePickerState, Int, Int) -> Unit
) {
    Dialog(
        onDismissRequest = { onDismissRequest() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .width(360.dp)
                .wrapContentHeight(), shape = RoundedCornerShape(24.dp)
        ) {
            Column {
                DatePicker(
                    state = datePickerState, showModeToggle = false,
                    headline = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val date = getDateInMillis(datePickerState.selectedDateMillis ?: 0L)
                            val (year, monthZeroIndexed, day, _, _) = date.extract()
                            Text(
                                text = String.format(
                                    "%d.%02d.%02d", year, monthZeroIndexed + 1, day,
                                ),
                                modifier = Modifier.padding(
                                    PaddingValues(
                                        start = 24.dp, end = 12.dp,
                                    ),
                                ),
                            )
                            //region Hour & Minute Picker
                            if (showTimePicker) {
                                FVerticalWheelPicker(
                                    count = 24,
                                    modifier = Modifier
                                        .width(60.dp)
                                        .height(90.dp),
                                    itemHeight = 30.dp,
                                    state = hourPickerState,
                                ) { index ->
                                    Text(
                                        text = String.format("%02d", index),
                                        style = TextStyle(fontSize = 20.sp),
                                    )
                                }
                                Text(
                                    ":",
                                    style = TextStyle(
                                        fontSize = 30.sp, fontWeight = FontWeight.ExtraLight,
                                    ),
                                    modifier = Modifier.padding(
                                        start = 4.dp, end = 4.dp, bottom = 1.dp,
                                    ),
                                )
                                FVerticalWheelPicker(
                                    count = 60,
                                    modifier = Modifier
                                        .width(60.dp)
                                        .height(90.dp),
                                    itemHeight = 30.dp,
                                    state = minutePickerState,
                                ) { index ->
                                    Text(
                                        text = String.format("%02d", index),
                                        style = TextStyle(fontSize = 20.sp),
                                    )
                                }
                            }
                            //endregion
                        }

                    },
                )
                IconButton(
                    onClick = {
                        onConfirm(
                            datePickerState,
                            hourPickerState.currentIndex,
                            minutePickerState.currentIndex
                        )
                    }, modifier = Modifier
                        .padding(end = 16.dp)
                        .align(Alignment.End)
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

@Composable
fun Category(
    currentCategory: Category?,
    categoryList: List<Category>,
    onAddCategory: (String, Int) -> Unit,
    onSelectCategory: (Category?) -> Unit
) {
    var showSelectCategoryDialog by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }

    var newCategoryTitle by remember { mutableStateOf("") }
    var newCategoryPriority by remember { mutableStateOf(3) }

    fun resetToDefault() {
        newCategoryTitle = ""
        newCategoryPriority = 3
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        TextButton(
            onClick = { showSelectCategoryDialog = true },
            modifier = Modifier
                .weight(1f)
                .padding(end = 20.dp)
                .bottomBorder(1.dp, color = Color.Gray)
        ) {
            Text(text = currentCategory?.title ?: "No Category")
        }
        IconButton(onClick = { onSelectCategory(null) }) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Deselect Category",
            )
        }
    }

    //region Select Category Dialog
    if (showSelectCategoryDialog) {
        Dialog(properties = DialogProperties(usePlatformDefaultWidth = false),
               onDismissRequest = { showSelectCategoryDialog = false }) {
            Surface(modifier = Modifier.fillMaxSize()) {
                Column {
                    // TODO: Will TopAppBar be in scaffold?
                    TopAppBar(showBackButton = true,
                              onBackPressed = { showSelectCategoryDialog = false },
                              title = { Text("Category") },
                              trailingContent = {
                                  IconButton(onClick = { showAddDialog = true }) {
                                      Icon(
                                          imageVector = Icons.Default.Add,
                                          contentDescription = "Add Category",
                                      )
                                  }
                              })

                    // Display Category List
                    LazyColumn(modifier = Modifier.padding(16.dp)) {
                        this.items(items = categoryList) { category ->
                            TextButton(
                                onClick = {
                                    onSelectCategory(category)
                                    showSelectCategoryDialog = false
                                }, modifier = Modifier.padding(8.dp)
                            ) {
                                Text(text = category.title)
                            }
                        }
                    }
                }

                //region Add Dialog
                if (showAddDialog) {
                    BottomSheetDialog(onDismissRequest = {
                        showAddDialog = false
                        resetToDefault()
                    }) {
                        Card(modifier = Modifier.fillMaxSize()) {
                            Column {
                                TopAppBar(showBackButton = false, onBackPressed = { }, title = {
                                    Text(
                                        "New Category",
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )
                                }, trailingContent = {
                                    IconButton(onClick = {
                                        onAddCategory(newCategoryTitle, newCategoryPriority)
                                        resetToDefault()
                                        showAddDialog = false
                                    }, modifier = Modifier.padding(horizontal = 8.dp)) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Add Category Confirm"
                                        )
                                    }
                                })
                                // TODO: Bottom Sheet 대신 작은 Dialog 로 변경
                                TextField(value = newCategoryTitle,
                                          onValueChange = { newCategoryTitle = it },
                                          placeholder = {
                                              Text(text = "Title")
                                          })

                                RatingBar(
                                    value = newCategoryPriority.toFloat(),
                                    onValueChange = { newCategoryPriority = it.toInt() },
                                    onRatingChanged = { },
                                    config = RatingBarConfig().size(40.dp)
                                )
                            }
                        }
                    }
                }
                //endregion
            }
        }
    }
    //endregion
}

@Preview(showBackground = true, name = "Todo Screen Preview")
@Composable
fun TodoScreenPreview() {
    EditPlanPage(
        editPlanViewModel = EditPlanViewModel(
            scheduleRepository = DummyScheduleRepository(),
            todoRepository = DummyTodoRepository(),
            categoryRepository = DummyCategoryRepository(),
            repeatGroupRepository = DummyRepeatGroupRepository(),
            )
    )
}

