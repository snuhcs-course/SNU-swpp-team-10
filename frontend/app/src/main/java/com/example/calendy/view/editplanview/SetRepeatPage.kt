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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.calendy.data.maindb.plan.PlanType
import com.example.calendy.data.maindb.repeatgroup.RepeatGroup
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun SetRepeat(uiState: EditPlanUiState, viewModel: EditPlanViewModel) {
    var isDialogOpen by remember { mutableStateOf(false) }
    val calendar = Calendar.getInstance().apply {
        time = if (uiState.entryType==PlanType.TODO) uiState.dueTime else uiState.startTime
    }
    var repeatGroup = uiState.repeatGroup

    if (isDialogOpen) {
        SetRepeatDialog(onDismiss = {
            isDialogOpen = false
        }, onRepeatGroup = {
            // uiState의 repeatGroup 정보 업데이트
            viewModel.setRepeatGroup(it)
        }, calendar, uiState.repeatGroup)
    }
    //uiState.RepeatGroup 정보 이용하여 repeatInfoText 값 변경
    fun updateRepeatInfoText(): String {
        if (repeatGroup==null) return "반복 안함"
        else {
            var endDate = ""
            var repeatInterval = ""
            var repeatRule = ""
            if (repeatGroup.endDate!=null) {
                val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
                endDate = dateFormat.format(repeatGroup.endDate) + "까지 "
            }
            if (repeatGroup.day) {
                repeatInterval = if (repeatGroup.repeatInterval==1) "매일 반복"
                else repeatGroup.repeatInterval.toString() + "일마다 반복"
                return endDate + repeatInterval
            }
            if (repeatGroup.week) {
                repeatInterval = if (repeatGroup.repeatInterval==1) "매주 "
                else repeatGroup.repeatInterval.toString() + "주마다 "
                val dayMapping = mapOf(
                    "MON" to "월요일",
                    "TUE" to "화요일",
                    "WED" to "수요일",
                    "THU" to "목요일",
                    "FRI" to "금요일",
                    "SAT" to "토요일",
                    "SUN" to "일요일"
                )
                val daysList = mutableListOf<String>()
                for (i in repeatGroup.repeatRule!!.indices step 3) {
                    val dayString = repeatGroup.repeatRule!!.substring(i, i + 3)
                    dayMapping[dayString]?.let {
                        dayMapping[dayString]?.let {
                            daysList.add(it)
                        }
                    }
                }
                repeatRule = daysList.joinToString(", ")
                return endDate + repeatInterval + repeatRule + "에 반복"

            }
            if (repeatGroup.month) {
                repeatInterval = if (repeatGroup.repeatInterval==1) "매월 "
                else repeatGroup.repeatInterval.toString() + "개월마다 "

                val dateList = mutableListOf<String>()
                for (i in repeatGroup.repeatRule!!.indices step 2) {
                    val dayString = repeatGroup.repeatRule!!.substring(i, i + 2).toInt()
                    dateList.add(dayString.toString() + "일")
                }
                repeatRule = dateList.joinToString(", ")
                return endDate + repeatInterval + repeatRule + "에 반복"

            }
            if (repeatGroup.year) {
                repeatInterval = if (repeatGroup.repeatInterval==1) "매년 반복"
                else repeatGroup.repeatInterval.toString() + "년마다 반복"
                return endDate + repeatInterval
            } else {
                return "반복 안함"
            }
        }
    }

    var repeatInfoText = remember { mutableStateOf(updateRepeatInfoText()) }
    // uiState의 repeatGroup 정보가 변경될 때마다 repeatInfoText 값을 업데이트
    LaunchedEffect(
        uiState.repeatGroup,
        uiState.repeatGroup?.day,
        uiState.repeatGroup?.week,
        uiState.repeatGroup?.month,
        uiState.repeatGroup?.year,
        uiState.repeatGroup?.repeatInterval,
        uiState.repeatGroup?.repeatRule,
        uiState.repeatGroup?.endDate
    ) {
        repeatInfoText.value = updateRepeatInfoText()
    }
    TextButton(
        onClick = { isDialogOpen = true },
        modifier = Modifier
//            .weight(1f)
            .fillMaxWidth(),
//                .padding(end = 20.dp)
//                .bottomBorder(1.dp, color = Color.Gray)
    ) {
        Text(
            text = repeatInfoText.value,
            textAlign = TextAlign.Left,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetRepeatDialog(
    onDismiss: () -> Unit,
    onRepeatGroup: (RepeatGroup?) -> Unit,
    calendar: Calendar,
    repeatGroup: RepeatGroup?
) {

    // repeatGroup table's repeatInt attribute value
    var repeatInterval = remember(repeatGroup) {
        mutableStateOf("").apply {
            if (repeatGroup==null) this.value = "1"
            else this.value = repeatGroup.repeatInterval.toString()
        }
    }
    // repeatGroup table's repeatRule attribute value - week ver
    val weeklyRule = remember(repeatGroup) {
        mutableStateListOf(*Array(7) { false }).apply {
            if (repeatGroup==null) {
                val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                this[if (dayOfWeek==1) 6 else dayOfWeek - 2] = true
            } else {
                //존재하던 repeatRule 반영 weeklyRule 값 변경 (해당하는 요일 index(0:월-6:일)의 값을 true로)
                if (repeatGroup.repeatRule!=null) {
                    if (repeatGroup.week) {
                        val dayMapping = mapOf(
                            "MON" to 0,
                            "TUE" to 1,
                            "WED" to 2,
                            "THU" to 3,
                            "FRI" to 4,
                            "SAT" to 5,
                            "SUN" to 6
                        )
                        for (i in repeatGroup.repeatRule!!.indices step 3) {
                            val dayString = repeatGroup.repeatRule!!.substring(i, i + 3)
                            dayMapping[dayString]?.let { index ->
                                this[index] = true
                            }
                        }
                    } else {
                        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                        this[if (dayOfWeek==1) 6 else dayOfWeek - 2] = true
                    }
                } else {
                    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                    this[if (dayOfWeek==1) 6 else dayOfWeek - 2] = true
                }
            }
        }
    }
    // repeatGroup table's repeatRule attribute value - month ver
    val monthlyRule = remember(repeatGroup) {
        mutableStateListOf(*Array(31) { false }).apply {
            if (repeatGroup==null) {
                val day = calendar.get(Calendar.DAY_OF_MONTH) - 1
                this[day] = true
            } else {
                //존재하던 repeatRule 반영 monthlyRule 값 변경 (해당하는 (날짜-1)(0-30) index의 값을 true로)
                if (repeatGroup.repeatRule!=null) {
                    if (repeatGroup.month) {
                        for (i in repeatGroup.repeatRule!!.indices step 2) {
                            val dayString = repeatGroup.repeatRule!!.substring(i, i + 2)
                            val dayIndex = dayString.toInt() - 1
                            if (dayIndex in 0..30) {
                                this[dayIndex] = true
                            }
                        }
                    } else {
                        val day = calendar.get(Calendar.DAY_OF_MONTH) - 1
                        this[day] = true
                    }
                } else {
                    val day = calendar.get(Calendar.DAY_OF_MONTH) - 1
                    this[day] = true
                }
            }
        }
    }
    // repeatGroup table's endDate attribute value
    var endPlanDate = remember(repeatGroup) {
        mutableStateOf<Long>(0).also { state ->
            if (repeatGroup==null) {
                state.value = calendar.timeInMillis
            } else if (repeatGroup.endDate==null) {
                when {
                    repeatGroup.day   -> {
                        calendar.add(Calendar.DATE, 7)
                        state.value = calendar.timeInMillis
                    }

                    repeatGroup.week  -> {
                        calendar.add(Calendar.MONTH, 1)
                        state.value = calendar.timeInMillis
                    }

                    repeatGroup.month -> {
                        calendar.add(Calendar.YEAR, 1)
                        state.value = calendar.timeInMillis
                    }

                    repeatGroup.year  -> {
                        calendar.add(Calendar.YEAR, 5)
                        state.value = calendar.timeInMillis
                    }
                }
            } else {
                repeatGroup.endDate?.let {
                    state.value = it.time
                }
            }
        }
    }
    // 반복 관련 radioGroup -> noRepeat : 반복 없음 / daily : day = true / monthly : month = true / yearly : year = true
    var repeatRadioGroup = remember(repeatGroup) {
        mutableStateOf("").apply {
            if (repeatGroup==null) {
                this.value = "noRepeat"
            } else {
                if (repeatGroup.day) this.value = "daily"
                if (repeatGroup.week) this.value = "weekly"
                if (repeatGroup.month) this.value = "monthly"
                if (repeatGroup.year) this.value = "yearly"
            }
        }
    }
    // 기간 관련 radioGroup ->
    val durationRadioGroup = remember(repeatGroup) {
        mutableStateOf("").apply {
            if (repeatGroup==null) {
                this.value = "noEndTime"
            } else {
                if (repeatGroup.endDate==null) this.value = "noEndTime"
                else this.value = "setEndTime"
            }
        }
    }

    //SetRepeatDialog 종료시 ui에 맞는 repeatGroup 객체를 넘겨줌
    fun exitDialog() {
        val endDate: Date? =
            if (durationRadioGroup.value=="noEndTime") null else Date(endPlanDate.value)
        val repeatInt = if (repeatInterval.value.isBlank()) 1 else repeatInterval.value.toInt()
        if (repeatGroup==null) {
            val newRepeatGroup: RepeatGroup? = when (repeatRadioGroup.value) {
                "noRepeat" -> null
                "daily"    -> RepeatGroup(
                    day = true,
                    week = false,
                    month = false,
                    year = false,
                    repeatInterval = repeatInt,
                    repeatRule = null,
                    endDate = endDate
                )

                "weekly"   -> RepeatGroup(
                    day = false,
                    week = true,
                    month = false,
                    year = false,
                    repeatInterval = repeatInt,
                    repeatRule = getRepeatRuleFromWeeklyRule(weeklyRule),
                    endDate = endDate
                )

                "monthly"  -> RepeatGroup(
                    day = false,
                    week = false,
                    month = true,
                    year = false,
                    repeatInterval = repeatInt,
                    repeatRule = getRepeatRuleFromMonthlyRule(monthlyRule),
                    endDate = endDate
                )
                //yearly
                else       -> RepeatGroup(
                    day = false,
                    week = false,
                    month = false,
                    year = true,
                    repeatInterval = repeatInt,
                    repeatRule = null,
                    endDate = endDate
                )
            }
            onRepeatGroup(newRepeatGroup)
        } else {
            // repeatgroup != null인 경우
            // TODO: 그냥 한 번에 해도 될 것 같다.
            if (repeatRadioGroup.value=="noRepeat") onRepeatGroup(null)
            else {
                when (repeatRadioGroup.value) {
                    "daily"   -> {
                        repeatGroup.day = true
                        repeatGroup.week = false
                        repeatGroup.month = false
                        repeatGroup.year = false
                        repeatGroup.repeatInterval = repeatInt
                        repeatGroup.repeatRule = null
                        repeatGroup.endDate = endDate
                    }

                    "weekly"  -> {
                        repeatGroup.day = false
                        repeatGroup.week = true
                        repeatGroup.month = false
                        repeatGroup.year = false
                        repeatGroup.repeatInterval = repeatInt
                        repeatGroup.repeatRule = getRepeatRuleFromWeeklyRule(weeklyRule)
                        repeatGroup.endDate = endDate
                    }

                    "monthly" -> {
                        repeatGroup.day = false
                        repeatGroup.week = false
                        repeatGroup.month = true
                        repeatGroup.year = false
                        repeatGroup.repeatInterval = repeatInt
                        repeatGroup.repeatRule = getRepeatRuleFromMonthlyRule(monthlyRule)
                        repeatGroup.endDate = endDate
                    }

                    "yearly"  -> {
                        repeatGroup.day = false
                        repeatGroup.week = false
                        repeatGroup.month = false
                        repeatGroup.year = true
                        repeatGroup.repeatInterval = repeatInt
                        repeatGroup.repeatRule = null
                        repeatGroup.endDate = endDate
                    }
                }
                onRepeatGroup(repeatGroup)
            }

        }
        onDismiss()
    }

    Dialog(
        onDismissRequest = { exitDialog() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn {
                item {
                    TopAppBar(modifier = Modifier.fillMaxWidth(), navigationIcon = {
                        IconButton(onClick = { exitDialog() }) {
                            Icon(Icons.Default.Close, contentDescription = null)
                        }
                    }, title = {
                        Text(
                            text = "  반복", style = MaterialTheme.typography.titleMedium
                        )
                    })
                }
                item {
                    Text(
                        text = "  반복 선택",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Divider()
                }
                item { RadioButtonLine("noRepeat", "반복 안 함", repeatRadioGroup) }
                item {
                    DailyRadioButtonLine(
                        "daily", "매일", "일마다", calendar, repeatInterval, repeatRadioGroup
                    )
                }
                item {
                    WeeklyRadioButtonLine(
                        "weekly",
                        "매주",
                        "주마다",
                        calendar,
                        repeatInterval,
                        repeatRadioGroup,
                        weeklyRule
                    )
                }
                item {
                    MonthlyRadioButtonLine(
                        "monthly",
                        "매월",
                        "개월마다",
                        calendar,
                        repeatInterval,
                        repeatRadioGroup,
                        monthlyRule,
                    )
                }
                item {
                    YearlyRadioButtonLine(
                        "yearly",
                        "매년",
                        "년마다",
                        calendar,
                        repeatInterval,
                        repeatRadioGroup,
                    )
                }



                if (repeatRadioGroup.value!="noRepeat") {
                    item {
                        Text(
                            text = "  기간",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Divider()
                    }
                    item { RadioButtonLine("noEndTime", "계속 반복", durationRadioGroup) }
                    item {
                        EndTimeRadioButtonLine(
                            "setEndTime", "종료 날짜", durationRadioGroup, endPlanDate
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun RadioButtonLine(type: String, text: String, selected: MutableState<String>) {
    Row(
        modifier = Modifier

            .fillMaxWidth()
            .padding(8.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected.value==type, onClick = {
            selected.value = type
        })
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text, style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.width(16.dp))

    }
}


@Composable
fun DailyRadioButtonLine(
    type: String,
    text: String,
    text2: String,
    calendar: Calendar,
    inputText: MutableState<String>,
    selected: MutableState<String>
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected.value==type, onClick = {
            selected.value = type
        })
        Spacer(modifier = Modifier.width(8.dp))
        if (selected.value==type) {
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
            Text(text = text2, style = MaterialTheme.typography.bodyMedium)
        } else {
            Text(text = text, style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(modifier = Modifier.width(16.dp))

    }
}

@Composable
fun WeeklyRadioButtonLine(
    type: String,
    text: String,
    text2: String,
    calendar: Calendar,
    inputText: MutableState<String>,
    selected: MutableState<String>,
    buttonStates: MutableList<Boolean>,
) {
    val daysOfWeek = listOf("월", "화", "수", "목", "금", "토", "일")

    Column(
        modifier = Modifier.animateContentSize()
    ) {
        DailyRadioButtonLine(type, text, text2, calendar, inputText, selected)

        // 선택되었을 때 펼쳐지는 ui
        AnimatedVisibility(visible = selected.value==type) {
            Text("  반복할 요일 선택", style = MaterialTheme.typography.bodyMedium)
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                daysOfWeek.forEachIndexed { index, day ->
                    TextButton(
                        onClick = {
                            if (buttonStates[index]) {
                                if (buttonStates.count { it } > 1) {
                                    // 이미 선택된 버튼이 둘 이상 있을 때만 해제
                                    // 즉, 선택된 버튼이 하나도 없는 상태를 방지
                                    buttonStates[index] = false
                                }
                            } else {
                                buttonStates[index] = true
                            }
                        },
//                        colors = if (buttonStates[index]) {
//                            ButtonDefaults.textButtonColors(containerColor = Color.LightGray)
//                        } else ButtonDefaults.textButtonColors(containerColor = Color.Transparent),
                        modifier = Modifier
                            .weight(1f)
                            .clip(CircleShape)
                            .padding(8.dp)
                    ) {
                        Text(text = day,
                             style = MaterialTheme.typography.labelSmall,
                             modifier = if (buttonStates[index]) Modifier.drawBehind {
                                 drawCircle(
                                     color = Color.LightGray,
                                     radius = this.size.maxDimension,
                                     style = Stroke(width = 6.dp.toPx())
                                 )
                             } else Modifier)

                    }
                }
            }
        }
    }
}

@Composable
fun MonthlyRadioButtonLine(
    type: String,
    text: String,
    text2: String,
    calendar: Calendar,
    inputText: MutableState<String>,
    selected: MutableState<String>,
    buttonStates: MutableList<Boolean>,
) {

    var isDrawerVisible by remember { mutableStateOf(false) }
    val days = (1..31).toList()

    Column(
        modifier = Modifier.animateContentSize(),
    ) {
        DailyRadioButtonLine(type, text, text2, calendar, inputText, selected)

        // 선택되었을 때 펼쳐지는 ui
        AnimatedVisibility(visible = selected.value==type) {
            Column() {
                Text("  반복할 날짜 선택", style = MaterialTheme.typography.bodyMedium)
                for (i in days.indices step 7) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        val endIndex = minOf(i + 7, days.size)
                        for (j in i until endIndex) {
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
//                                colors = if (buttonStates[j]) {
//                                    ButtonDefaults.textButtonColors(containerColor = Color.LightGray)
//                                } else ButtonDefaults.textButtonColors(containerColor = Color.Transparent),
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(CircleShape)
                                    .padding(4.dp)
                            ) {
                                //Text(text = days[j].toString(), fontSize = 14.sp)
                                Text(text = days[j].toString(), style = MaterialTheme.typography.labelSmall,
                                     modifier = if (buttonStates[j]) Modifier.drawBehind {
                                         drawCircle(
                                             color = Color.LightGray,
                                             radius = this.size.maxDimension,
                                             style = Stroke(width = 3.dp.toPx())
                                         )
                                     } else Modifier)
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

@Composable
fun YearlyRadioButtonLine(
    type: String,
    text: String,
    text2: String,
    calendar: Calendar,
    inputText: MutableState<String>,
    selected: MutableState<String>,
) {

    Column(
        modifier = Modifier.animateContentSize(),
    ) {
        DailyRadioButtonLine(type, text, text2, calendar, inputText, selected)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EndTimeRadioButtonLine(
    type: String, text: String, selected: MutableState<String>, endDate: MutableState<Long>
) {
    Column(
        modifier = Modifier.animateContentSize(),
    ) {
        RadioButtonLine(type = type, text = text, selected = selected)
        AnimatedVisibility(visible = selected.value==type) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val state = rememberDatePickerState(
                    initialSelectedDateMillis = endDate.value,
                    initialDisplayMode = DisplayMode.Picker
                )
                DatePicker(state = state, modifier = Modifier.padding(16.dp))

                LaunchedEffect(state.selectedDateMillis) {
                    state.selectedDateMillis?.let {
                        val calendar = Calendar.getInstance().apply {
                            timeInMillis = it
                            set(Calendar.HOUR_OF_DAY, 23)
                            set(Calendar.MINUTE, 59)
                            set(Calendar.SECOND, 59)
                            set(Calendar.MILLISECOND, 999)
                        }
                        endDate.value = calendar.timeInMillis
                    }
                }
            }
        }
    }
}


fun getRepeatRuleFromMonthlyRule(monthlyRule: List<Boolean>): String {
    val builder = StringBuilder()
    for (i in monthlyRule.indices) {
        if (monthlyRule[i]) {
            // 인덱스 값을 기반으로 실제 날짜 값을 얻고, 이를 2자리 문자열로 변환
            val day = (i + 1).toString().padStart(2, '0')
            builder.append(day)
        }
    }
    return builder.toString()
}

fun getRepeatRuleFromWeeklyRule(weeklyRule: List<Boolean>): String {
    val dayMapping = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
    val builder = StringBuilder()

    for (i in weeklyRule.indices) {
        if (weeklyRule[i]) {
            builder.append(dayMapping[i])
        }
    }
    return builder.toString()
}



