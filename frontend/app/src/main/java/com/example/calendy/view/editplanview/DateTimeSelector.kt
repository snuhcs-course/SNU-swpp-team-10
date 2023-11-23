package com.example.calendy.view.editplanview

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRightAlt
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calendy.utils.DateHelper
import com.example.calendy.utils.DateHelper.extract
import com.example.calendy.utils.DateHelper.timestampUTC
import com.sd.lib.compose.wheel_picker.FVerticalWheelPicker
import com.sd.lib.compose.wheel_picker.rememberFWheelPickerState
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

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
private fun HourMinutePicker(
    currentHour: Int, currentMinute: Int, onValueChanged: (hour: Int, minute: Int) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 16.dp)
    ) {
        VerticalWheelPickerWrapper(
            value = currentHour,
            onValueChanged = { onValueChanged(it, currentMinute) },
            itemCount = 24,
            wheelItem = { value ->
                Text(
                    text = String.format("%02d", value),
                    style = TextStyle(fontSize = 22.sp),
                )
            },
            wheelItemHeight = 30.dp,
            modifier = Modifier.width(36.dp),
        )
        Text(
            ":",
            style = TextStyle(
                fontSize = 20.sp, fontWeight = FontWeight.Normal,
            ),
            modifier = Modifier.padding(
                start = 4.dp, end = 4.dp, bottom = 1.dp
            ),
        )
        VerticalWheelPickerWrapper(
            value = currentMinute,
            onValueChanged = { onValueChanged(currentHour, it) },
            itemCount = 60,
            wheelItem = { value ->
                Text(
                    text = String.format("%02d", value),
                    style = TextStyle(fontSize = 22.sp),
                )
            },
            wheelItemHeight = 30.dp,
            modifier = Modifier.width(36.dp),
        )
    }
}

@Composable
private fun DateTimeSelectButton(
    currentTime: Date,
    openDateDialog: () -> Unit,
    toggleTimePicker: () -> Unit,
    isVertical: Boolean = false,
) {
    val hour = currentTime.hours
    val minute = currentTime.minutes

    val dateButtonAndTimeButton: @Composable () -> Unit = {
        TextButton(
            onClick = { openDateDialog() },
            modifier = Modifier.wrapContentWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent, contentColor = Color.Black
            ),
        ) {
            val formatter = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
            val textString = formatter.format(currentTime)
            Text(text = textString, style = TextStyle(fontSize = 20.sp))
        }
        TextButton(
            onClick = { toggleTimePicker() },
            modifier = Modifier.wrapContentWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent, contentColor = Color.Black
            ),
        ) {
            Text(
                text = String.format("%02d:%02d", hour, minute), style = TextStyle(fontSize = 20.sp)
            )
        }
    }

    if (isVertical) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.wrapContentHeight()
        ) {
            dateButtonAndTimeButton()
        }
    } else {
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            dateButtonAndTimeButton()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OneDateSelectorButton(
    time: Date,
    onSelectTime: (Date) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hour = time.hours
    val minute = time.minutes
    val utcTime = time.timestampUTC()

    var dateDialogOpen by remember { mutableStateOf(false) }
    var timePickerOpen by remember { mutableStateOf(false) }

    fun openDateDialog() {
        dateDialogOpen = true
    }

    fun closeDateDialog() {
        dateDialogOpen = false
    }

    fun toggleTimePicker() {
        timePickerOpen = !timePickerOpen
    }


    DateTimeSelectButton(
        currentTime = time,
        openDateDialog = { openDateDialog() },
        toggleTimePicker = { toggleTimePicker() },
    )

    val datePickerState = rememberDatePickerState()
    // if time Change -> picker state is updated
    datePickerState.setSelection(utcTime)
    // date state -> time Change
    fun updateTime(newHour: Int? = null, newMinute: Int? = null) {
        val newTime = DateHelper.getDateFromUTCMillis(
            dateInUTCMillis = datePickerState.selectedDateMillis ?: utcTime,
            hourOfDay = newHour ?: hour,
            minute = newMinute ?: minute,
        )

        onSelectTime(newTime)
    }

    if (dateDialogOpen) {
        DatePickerDialog(onDismissRequest = { closeDateDialog() }, confirmButton = {
            TextButton(onClick = {
                updateTime()
                closeDateDialog()
            }) {
                Text("확인")
            }
        }) {
            DatePicker(
                state = datePickerState, showModeToggle = false,
            )
        }
    }
    if (timePickerOpen) {
        Box(
            contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()
        ) {
            HourMinutePicker(currentHour = hour,
                             currentMinute = minute,
                             onValueChanged = { newHour, newMinute ->
                                 updateTime(newHour, newMinute)
                             })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TwoDateSelectorButton(
    startTime: Date,
    endTime: Date,
    onSelectTimeRange: (Date, Date) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectingStart by remember { mutableStateOf(false) }
    val hour = if (selectingStart) startTime.hours else endTime.hours
    val minute = if (selectingStart) startTime.minutes else endTime.minutes
    val utcTime = if (selectingStart) startTime.timestampUTC() else endTime.timestampUTC()

    var dateDialogOpen by remember { mutableStateOf(false) }
    var timePickerOpen by remember { mutableStateOf(false) }
    var forceRecompose by remember { mutableStateOf(0) }

    fun openDateDialog(isStart: Boolean) {
        dateDialogOpen = true
        selectingStart = isStart
    }

    fun closeDateDialog() {
        dateDialogOpen = false
    }

    fun toggleTimePicker(isStart: Boolean) {
        if (selectingStart==isStart) {
            timePickerOpen = !timePickerOpen
        } else {
            timePickerOpen = true
        }
        selectingStart = isStart
    }


    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        DateTimeSelectButton(currentTime = startTime,
                             openDateDialog = { openDateDialog(true); },
                             toggleTimePicker = { toggleTimePicker(true); },
                             isVertical = true
        )
        Icon(imageVector = Icons.Filled.ArrowRightAlt, contentDescription = null)
        DateTimeSelectButton(currentTime = endTime,
                             openDateDialog = { openDateDialog(false); },
                             toggleTimePicker = { toggleTimePicker(false); },
                             isVertical = true
        )
    }


    val datePickerState = rememberDatePickerState()
    // if time Change -> picker state is updated
    datePickerState.setSelection(utcTime)
    // date state -> time Change
    fun updateTime(newHour: Int? = null, newMinute: Int? = null) {
        val newTime = DateHelper.getDateFromUTCMillis(
            dateInUTCMillis = datePickerState.selectedDateMillis ?: utcTime,
            hourOfDay = newHour ?: hour,
            minute = newMinute ?: minute,
        )

        if (selectingStart) {
            // Sync EndDate if user change startDate (Not Start Time)
            val dateShiftedEndTime = if (newHour!==null) endTime else {
                val (year, monthZeroIndexed, date, _, _) = newTime.extract()
                DateHelper.getDate(year, monthZeroIndexed, date, endTime.hours, endTime.minutes)
            }

            if (newTime.before(dateShiftedEndTime)) {
                // startTime < endTime
                onSelectTimeRange(newTime, dateShiftedEndTime)
            } else {
                // startTime >= endTime
                // 2. Change Date and Time to startTime
                onSelectTimeRange(newTime, newTime)
            }
        } else {
            if (startTime.before(newTime)) {
                // startTime < endTime
                onSelectTimeRange(startTime, newTime)
            } else {
                // startTime >= endTime
                // newTime.dateOnly cannot be before startTime.dateOnly (Because of DatePickerDialog validation)
                onSelectTimeRange(startTime, startTime)
            }
        }
    }

    if (dateDialogOpen) {
        DatePickerDialog(onDismissRequest = { closeDateDialog() }, confirmButton = {
            TextButton(onClick = {
                updateTime()
                closeDateDialog()
            }) {
                Text("확인")
            }
        }) {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            val (year, monthZeroIndexed, date, _, _) = startTime.extract()
            val startDate = calendar.apply {
                clear()
                set(year, monthZeroIndexed, date, 0, 0)
            }.timeInMillis
            DatePicker(state = datePickerState, showModeToggle = false, dateValidator = { date ->
                if (selectingStart==false) {
                    date >= startDate
                } else {
                    true
                }
            })
        }
    }
    if (timePickerOpen) {
        Box(
            contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()
        ) {
            key(forceRecompose) {
                HourMinutePicker(
                    currentHour = hour,
                    currentMinute = minute,
                    onValueChanged = { newHour, newMinute ->
                        if (hour != newHour || minute != newMinute) {
                            updateTime(newHour, newMinute)
                            forceRecompose = 1
                        } else {
                            forceRecompose = 0
                        }
                    },
                )
            }
        }
    }
}

@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true, name = "Date Selector Preview")
@Composable
fun DateSelectorPreview() {
    OneDateSelectorButton(
        time = Date(),
        onSelectTime = { },
    )
}

@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true, name = "Date Selector Preview")
@Composable
fun DateRangeSelectorPreview() {
    TwoDateSelectorButton(
        startTime = Date(),
        endTime = Date(),
        onSelectTimeRange = { _, _ -> },
    )
}