package com.example.calendy.view.editplanview

import android.icu.util.Calendar
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.window.Dialog
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.calendy.utils.bottomBorder


@Composable

fun SetRepeat(uiState: EditPlanUiState) {
    var isDialogOpen by remember { mutableStateOf(false) }
    var repeatIntervalText by remember { mutableStateOf("반복 안 함") }
    val calendar = Calendar.getInstance().apply { time = uiState.startTime }

    FieldWithLeadingText(leadingText = "반복") {
        TextButton(
            onClick = { isDialogOpen = true },
            modifier = Modifier
                .padding(end = 20.dp)
                .bottomBorder(1.dp, color = Color.Gray)
        ) {
            Text(text = repeatIntervalText)
        }
    }

    if (isDialogOpen) {
        SetRepeatDialog(onDismiss = {
            isDialogOpen = false
        }, calendar)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetRepeatDialog(onDismiss: () -> Unit, calendar: Calendar) {

    // repeatGroup table's repeatInt attribute value
    var repeatInterval = remember { mutableStateOf("") }
    // 반복, 기간 2개의 라디오그룹 설정
    val repeatRadioGroup = remember { mutableStateOf(1) }
    val durationRadioGroup = remember { mutableStateOf(1) }
    Dialog(
        onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn {
                item {
                    TopAppBar(modifier = Modifier.fillMaxWidth(), navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = null)
                        }
                    }, title = { Text("반복") })
                }
                item {
                    Text(
                        text = "반복 선택",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Divider()
                }
                item { RadioButtonLine("반복 안 함", 1,repeatRadioGroup) }
                item {
                    DailyRadioButtonLine(
                        "매일", "일마다", 2, calendar, repeatInterval, repeatRadioGroup
                    )
                }
                item {
                    WeeklyRadioButtonLine(
                        "매주", "주마다",3, calendar, repeatInterval, repeatRadioGroup
                    )
                }
                item {
                    MonthlyRadioButtonLine(
                        "매월", "개월마다",4, calendar, repeatInterval, repeatRadioGroup
                    )
                }
                item {
                    YearlyRadioButtonLine(
                        "매년", "년마다", 5, calendar, repeatInterval, repeatRadioGroup
                    )
                }



                if (repeatRadioGroup.value!= 1) {
                    item {
                        Text(
                            text = "기간",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Divider()
                    }
                    item { RadioButtonLine("계속 반복", 1,durationRadioGroup) }
                    item {
                        RepeatTimeRadioButtonLine(
                            "반복 횟수", "번 반복",2, calendar, durationRadioGroup
                        )
                    }
                    item { EndTimeRadioButtonLine("종료 날짜", 3,durationRadioGroup) }
                }
            }
        }
    }
}


@Composable
fun RadioButtonLine(text: String,idx: Int, selected: MutableState<Int>) {
    Row(
        modifier = Modifier

            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected.value==idx, onClick = {
            selected.value = idx
        })
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text)

        Spacer(modifier = Modifier.width(16.dp))

    }
}


@Composable
fun DailyRadioButtonLine(
    text: String,
    text2: String,
    idx: Int,
    calendar: Calendar,
    inputText: MutableState<String>,
    selected: MutableState<Int>
) {
    LaunchedEffect(selected.value) {
        if (selected.value==idx) {
            inputText.value = "1"
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected.value==idx, onClick = {
            selected.value = idx
        })
        Spacer(modifier = Modifier.width(8.dp))
        if (selected.value==idx) {
            TextField(
                value = inputText.value,
                modifier = Modifier.width(50.dp),
                onValueChange = { inputValue ->
                    val filtered = inputValue.filter { it.isDigit() } // 숫자만 허용
                    if (filtered.length <= 2) {
                        inputText.value = filtered
                    } // 입력 자리수 2자리로 제한
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent

                ),
            )
            Text(text = text2)
        } else {
            Text(text = text)
        }
        Spacer(modifier = Modifier.width(16.dp))

    }
}

@Composable
fun WeeklyRadioButtonLine(
    text: String,
    text2: String,
    idx: Int,
    calendar: Calendar,
    inputText: MutableState<String>,
    selected: MutableState<Int>
) {
    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    val daysOfWeek = listOf("월", "화", "수", "목", "금", "토", "일")
    // startTime에 해당하는 요일
    val selectedDay = daysOfWeek[if (dayOfWeek==1) 6 else dayOfWeek - 2]
    // 각 TextButton의 클릭 상태를 관리하는 리스트
    val buttonStates = remember { mutableStateListOf(*Array(daysOfWeek.size) { false }) }
    Column(
        modifier = Modifier.animateContentSize()
    ) {
        DailyRadioButtonLine(text, text2, idx, calendar, inputText, selected)

        // 선택되었을 때 펼쳐지는 ui
        AnimatedVisibility(visible = selected.value==idx) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                daysOfWeek.forEachIndexed { index, day ->
                    if (day==selectedDay) {
                        buttonStates[index] = true
                    }
                    TextButton(
                        onClick = {
                            if (buttonStates[index]) {
                                if (buttonStates.count { it } > 1) {
                                    buttonStates[index] = false
                                }
                            } else {
                                buttonStates[index] = true
                            }
                        },
                        colors = if (buttonStates[index]) {
                            ButtonDefaults.textButtonColors(containerColor = Color.LightGray)
                        } else ButtonDefaults.textButtonColors(containerColor = Color.Transparent),
                        modifier = Modifier
                            .weight(1f)
                            .clip(CircleShape)
                            .padding(8.dp)
                    ) {
                        Text(text = day)
                    }
                }
            }
        }
    }
}

@Composable
fun MonthlyRadioButtonLine(
    text: String,
    text2: String,
    idx: Int,
    calendar: Calendar,
    inputText: MutableState<String>,
    selected: MutableState<Int>
) {

    var isDrawerVisible by remember { mutableStateOf(false) }
    var selectedButtonIdx by remember { mutableStateOf<Int?>(null) }
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val days = (1..31).toList()
    val buttonStates = remember { mutableStateListOf(*Array(days.size) { false }) }

    Column(
        modifier = Modifier.animateContentSize(),
    ) {
        DailyRadioButtonLine(text, text2, idx,calendar, inputText, selected)

        // 선택되었을 때 펼쳐지는 ui
        AnimatedVisibility(visible = selected.value==idx) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                TextButton(
                    onClick = { selectedButtonIdx = 1 }, colors = if (selectedButtonIdx==1) {
                        ButtonDefaults.textButtonColors(containerColor = Color.LightGray)
                    } else ButtonDefaults.textButtonColors(containerColor = Color.Transparent)
                ) {
                    Text(text = "" + day + "일 마다 반복")

                }
                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = { selectedButtonIdx = 2 }, colors = if (selectedButtonIdx==2) {
                        ButtonDefaults.textButtonColors(containerColor = Color.LightGray)
                    } else ButtonDefaults.textButtonColors(containerColor = Color.Transparent)
                ) {
                    Text(text = "달의 마지막 날마다 반복")
                }
                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = {
                        selectedButtonIdx = 3
                        isDrawerVisible = !isDrawerVisible
                    }, colors = if (selectedButtonIdx==3) {
                        ButtonDefaults.textButtonColors(containerColor = Color.LightGray)
                    } else ButtonDefaults.textButtonColors(containerColor = Color.Transparent)
                ) {
                    Text(text = "반복할 날짜 선택")
                    if (selectedButtonIdx!=3) {
                        isDrawerVisible = false
                    }
                }
                // 반복 날짜 설정 버튼 클릭시 펼쳐지는 ui
                AnimatedVisibility(visible = isDrawerVisible) {
                    Column {
                        for (i in days.indices step 7) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                val endIndex = minOf(i + 7, days.size)
                                for (j in i until endIndex) {
                                    if (day - 1==j) {
                                        buttonStates[j] = true
                                    }
                                    TextButton(
                                        onClick = {
                                            if (buttonStates[j]) {
                                                if (buttonStates.count { it } > 1) {
                                                    buttonStates[j] = false
                                                }
                                            } else {
                                                buttonStates[j] = true
                                            }
                                        },
                                        colors = if (buttonStates[j]) {
                                            ButtonDefaults.textButtonColors(containerColor = Color.LightGray)
                                        } else ButtonDefaults.textButtonColors(containerColor = Color.Transparent),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(CircleShape)
                                            .padding(4.dp)
                                    ) {
                                        Text(text = days[j].toString(), fontSize = 14.sp)
                                    }
                                }
                                if (endIndex==31) {
                                    repeat(4) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }

                        }
                    }

                }
            }
        }
    }
}

@Composable
fun YearlyRadioButtonLine(
    text: String,
    text2: String,
    idx: Int,
    calendar: Calendar,
    inputText: MutableState<String>,
    selected: MutableState<Int>
) {

    var isDrawerVisible by remember { mutableStateOf(false) }
    var selectedButtonIdx by remember { mutableStateOf<Int?>(null) }
    val month = calendar.get(Calendar.MONTH) + 1
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val months = listOf(
        "1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"
    )
    val buttonStates = remember { mutableStateListOf(*Array(months.size) { false }) }

    Column(
        modifier = Modifier.animateContentSize(),
    ) {
        DailyRadioButtonLine(text, text2, idx,calendar, inputText, selected)

        // 선택되었을 때 펼쳐지는 ui
        AnimatedVisibility(visible = selected.value==idx) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                TextButton(
                    onClick = { selectedButtonIdx = 1 }, colors = if (selectedButtonIdx==1) {
                        ButtonDefaults.textButtonColors(containerColor = Color.LightGray)
                    } else ButtonDefaults.textButtonColors(containerColor = Color.Transparent)
                ) {
                    Text(text = "" + month + "월 " + day + "일에 반복")
                }
                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = {
                        selectedButtonIdx = 2
                        isDrawerVisible = !isDrawerVisible
                    }, colors = if (selectedButtonIdx==2) {
                        ButtonDefaults.textButtonColors(containerColor = Color.LightGray)
                    } else ButtonDefaults.textButtonColors(containerColor = Color.Transparent)
                ) {
                    Text(text = "" + day + "일에 반복할 월 선택")
                    if (selectedButtonIdx!=2) {
                        isDrawerVisible = false
                    }
                }
                // 반복 월 설정 버튼 선택시 펼쳐지는 항목
                AnimatedVisibility(visible = isDrawerVisible) {
                    Column {
                        for (i in months.indices step 6) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                val endIndex = minOf(i + 6, months.size)
                                for (j in i until endIndex) {
                                    if (month - 1==j) {
                                        buttonStates[j] = true
                                    }
                                    TextButton(
                                        onClick = {
                                            if (buttonStates[j]) {
                                                if (buttonStates.count { it } > 1) {
                                                    buttonStates[j] = false
                                                }
                                            } else {
                                                buttonStates[j] = true
                                            }
                                        },
                                        colors = if (buttonStates[j]) {
                                            ButtonDefaults.textButtonColors(containerColor = Color.LightGray)
                                        } else ButtonDefaults.textButtonColors(containerColor = Color.Transparent),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(CircleShape)
                                            .padding(4.dp)
                                    ) {
                                        Text(text = months[j], fontSize = 14.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun RepeatTimeRadioButtonLine(
    text: String, text2: String, idx: Int, calendar: Calendar, selected: MutableState<Int>
) {
    val repeatTime = remember { mutableStateOf("") }
    DailyRadioButtonLine(text, text2, idx ,calendar, repeatTime, selected)
}

@Composable
fun EndTimeRadioButtonLine(text: String,idx: Int, selected: MutableState<Int>) {
    Column(
        modifier = Modifier.animateContentSize(),
    ) {
        RadioButtonLine(text = text, idx = idx ,selected = selected)
        AnimatedVisibility(visible = selected.value==idx) {

        }
    }
}


fun convertDayOfWeekToKor(dayOfWeek: Int): String {
    return when (dayOfWeek) {
        Calendar.SUNDAY    -> "일요일"
        Calendar.MONDAY    -> "월요일"
        Calendar.TUESDAY   -> "화요일"
        Calendar.WEDNESDAY -> "수요일"
        Calendar.THURSDAY  -> "목요일"
        Calendar.FRIDAY    -> "금요일"
        Calendar.SATURDAY  -> "토요일"
        else               -> "알 수 없는 요일"
    }
}

fun convertDayOfWeekToEng(dayOfWeek: Int): String {
    return when (dayOfWeek) {
        Calendar.SUNDAY    -> "SUN"
        Calendar.MONDAY    -> "MON"
        Calendar.TUESDAY   -> "TUE"
        Calendar.WEDNESDAY -> "WED"
        Calendar.THURSDAY  -> "THU"
        Calendar.FRIDAY    -> "FRI"
        Calendar.SATURDAY  -> "SUN"
        else               -> "UNKNOWN"
    }
}



