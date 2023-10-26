package com.example.calendy.view.editplanview

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.calendy.utils.DateHelper
import com.example.calendy.utils.DateHelper.extract
import com.sd.lib.compose.wheel_picker.FVerticalWheelPicker
import com.sd.lib.compose.wheel_picker.rememberFWheelPickerState
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

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
    // TODO: Calendar 대신 uiState 날짜 사용
    val calendar = Calendar.getInstance()

    // isDialogOpen: Daily Date Picker & Date And Time Picker
    var isDialogOpen by remember { mutableStateOf(false) }
    fun openDialog() {
        isDialogOpen = true
    }

    fun closeDialog() {
        isDialogOpen = false
    }

    val (year, monthZeroIndexed, _, hour, minute) = dueTime.extract()
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dueTime.time)
    // if dueTime Change -> date state is set
    datePickerState.setSelection(dueTime.time)
    // date state -> dueTime Change
    fun updateDueTime(timeInMillis: Long?, hour: Int, minute: Int) {
        calendar.timeInMillis = timeInMillis ?: calendar.timeInMillis
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        onSelectDueTime(calendar.time)
    }

    // TODO: Date Picker view month change when (year, month) change

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        //region 3 Buttons
        val shape = RoundedCornerShape(8.dp)
        Row(
            modifier = Modifier.height(32.dp)
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
                YearPicker(
                    currentValue = year,
                    onValueChanged = {
                        onSelectDueYear(it)
                    },
                )
            }
            //endregion
            //region isMonthly -> Pick Year & Month
            else if (isMonthly) {
                Row(
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    YearPicker(
                        currentValue = year,
                        onValueChanged = {
                            onSelectDueMonth(it, monthZeroIndexed)
                        },
                    )
                    MonthPicker(
                        currentValue = monthZeroIndexed,
                        onValueChanged = {
                            onSelectDueMonth(year, it)
                        },
                    )
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
                    val textString = formatter.format(dueTime)
                    Text(text = textString, style = TextStyle(fontSize = 20.sp))
                }

                if (isDialogOpen) {
                    DateTimePickerDialog(
                        datePickerState = datePickerState,
                        showTimePicker = false,
                        onDismissRequest = {
                            closeDialog()
                        },
                        onConfirm = { datePickerState, _, _ ->
                            closeDialog()
                            updateDueTime(
                                timeInMillis = datePickerState.selectedDateMillis,
                                hour = hour,
                                minute = minute
                            )
                        },
                    )
                }
            }
            //endregion
            //region Date & Time Picker
            else {
                // Neither isYearly, isMonthly, isDaily
                Button(
                    onClick = { openDialog() },
                    modifier = Modifier.align(Alignment.BottomCenter),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent, contentColor = Color.Black
                    ),
                ) {
                    val formatter = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
                    val textString = formatter.format(dueTime)
                    Text(text = textString, style = TextStyle(fontSize = 20.sp))
                }

                if (isDialogOpen) {
                    DateTimePickerDialog(
                        datePickerState = datePickerState,
                        showTimePicker = true,
                        initialHour = hour,
                        initialMinute = minute,
                        onDismissRequest = {
                            closeDialog()
                        },
                        onConfirm = { datePickerState, hour, minute ->
                            closeDialog()
                            updateDueTime(
                                timeInMillis = datePickerState.selectedDateMillis,
                                hour = hour,
                                minute = minute
                            )
                        },
                    )
                }
            }
            //endregion
        }
    }
}

@Composable
private fun VerticalWheelPickerWrapper(
    value: Int,
    onValueChanged: (value: Int) -> Unit,
    itemCount: Int,
    wheelItem: @Composable (value: Int) -> Unit,
    wheelItemWidth: Dp = 60.dp,
    wheelItemHeight: Dp = 20.dp,
    valueToIndex: (value: Int) -> Int = { it },
    indexToValue: (index: Int) -> Int = { it },
    @SuppressLint("ModifierParameter")
    modifier: Modifier = Modifier
        .width(wheelItemWidth)
        .height(wheelItemHeight * 3),
) {
    val fPickerState = rememberFWheelPickerState(initialIndex = value)
    // value -> Picker update
    LaunchedEffect(key1 = value) {
        fPickerState.scrollToIndex(valueToIndex(value))
    }
    // Picker -> value update
    LaunchedEffect(key1 = fPickerState.currentIndex) {
        onValueChanged(indexToValue(fPickerState.currentIndex))
    }

    FVerticalWheelPicker(
        modifier = modifier, itemHeight = wheelItemHeight, count = itemCount, state = fPickerState
    ) { index ->
        wheelItem(value = indexToValue(index))
    }
}

@Composable
private fun YearPicker(currentValue: Int, onValueChanged: (value: Int) -> Unit) {
    VerticalWheelPickerWrapper(
        value = currentValue,
        onValueChanged = onValueChanged,
        itemCount = 100,
        wheelItem = { value -> Text(value.toString()) },
        valueToIndex = { value -> value - 2001 },
        indexToValue = { index -> index + 2001 },
    )
}

@Composable
private fun MonthPicker(currentValue: Int, onValueChanged: (value: Int) -> Unit) {
    VerticalWheelPickerWrapper(
        value = currentValue,
        onValueChanged = onValueChanged,
        itemCount = 12,
        wheelItem = { value -> Text((value + 1).toString()) },
    )
}

@Composable
private fun HourMinutePicker(
    currentHour: Int, currentMinute: Int, onValueChanged: (hour: Int, minute: Int) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        VerticalWheelPickerWrapper(
            value = currentHour,
            onValueChanged = { onValueChanged(it, currentMinute) },
            itemCount = 24,
            wheelItem = { value ->
                Text(
                    text = String.format("%02d", value),
                    style = TextStyle(fontSize = 20.sp),
                )
            },
            wheelItemHeight = 30.dp,
        )
        Text(
            ":",
            style = TextStyle(
                fontSize = 30.sp, fontWeight = FontWeight.ExtraLight,
            ),
            modifier = Modifier.padding(
                start = 4.dp, end = 4.dp, bottom = 1.dp,
            ),
        )
        VerticalWheelPickerWrapper(
            value = currentMinute,
            onValueChanged = { onValueChanged(currentHour, it) },
            itemCount = 60,
            wheelItem = { value ->
                Text(
                    text = String.format("%02d", value),
                    style = TextStyle(fontSize = 20.sp),
                )
            },
            wheelItemHeight = 30.dp,
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DateTimePickerDialog(
    datePickerState: DatePickerState,
    showTimePicker: Boolean,
    initialHour: Int = 0,
    initialMinute: Int = 0,
    onDismissRequest: () -> Unit,
    onConfirm: (DatePickerState, Int, Int) -> Unit
) {
    var hour by remember {
        mutableStateOf(initialHour)
    }
    var minute by remember {
        mutableStateOf(initialMinute)
    }

    Dialog(
        onDismissRequest = { onDismissRequest() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.width(360.dp).wrapContentHeight(), shape = RoundedCornerShape(24.dp)
        ) {
            Column {
                DatePicker(
                    state = datePickerState, showModeToggle = false,
                    headline = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val date =
                                DateHelper.getDateInMillis(datePickerState.selectedDateMillis ?: 0L)
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
                            if (showTimePicker) {
                                HourMinutePicker(
                                    currentHour = hour,
                                    currentMinute = minute,
                                    onValueChanged = { newHour, newMinute ->
                                        hour = newHour
                                        minute = newMinute
                                    },
                                )
                            }
                        }

                    },
                )
                IconButton(
                    onClick = {
                        onConfirm(
                            datePickerState, hour, minute
                        )
                    }, modifier = Modifier.padding(end = 16.dp).align(Alignment.End)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangeSelector(modifier: Modifier = Modifier) {
    var isDialogOpen by remember { mutableStateOf(false) }
    fun openDialog() {
        isDialogOpen = true
    }

    fun closeDialog() {
        isDialogOpen = false
    }

    // TODO: Calendar 대신 uiState 날짜 사용
    val calendar = Calendar.getInstance()

    val dateRangePickerState = rememberDateRangePickerState()

    Button(
        onClick = { openDialog() },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent, contentColor = Color.Black
        ),
    ) {
        Text(text = "BUTTON", style = TextStyle(fontSize = 20.sp))
    }

    if (isDialogOpen) {
        Dialog(
            onDismissRequest = { closeDialog() },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier.width(360.dp).wrapContentHeight(),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column {
                    DateRangePicker(
                        state = dateRangePickerState, modifier = Modifier.height(480.dp)
                    )
                    Text("HELLO")
                }
            }
        }
    }
}