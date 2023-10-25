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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.calendy.data.plan.Plan
import com.example.calendy.data.repeatgroup.RepeatGroup
import com.example.calendy.utils.bottomBorder
import java.util.Date


@Composable
fun SetRepeat(uiState: EditPlanUiState, viewModel: EditPlanViewModel) {
    var isDialogOpen by remember { mutableStateOf(false) }
    var repeatInfoText = remember { mutableStateOf("반복 안 함") }
    val calendar = Calendar.getInstance().apply {
        time = if (uiState.entryType==Plan.PlanType.Todo) uiState.dueTime else uiState.startTime
    }
    val repeatGroup = uiState.repeatGroup

    Row(modifier = Modifier.fillMaxWidth()) {
        TextButton(
            onClick = { isDialogOpen = true },
            modifier = Modifier
                .weight(1f)
                .padding(end = 20.dp)
                .bottomBorder(1.dp, color = Color.Gray)
        ) {
            Text(text = repeatInfoText.value)
        }
        IconButton(onClick = { /* TODO: onClick Deselect */ }) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Deselect Repeat",
            )
        }
    }


    if (isDialogOpen) {
//        SetRepeatDialog(onDismiss = {
//            isDialogOpen = false
//        }, calendar, RepeatGroup(0,false,true,false,false, 2, "", null))
        SetRepeatDialog(onDismiss = {
            isDialogOpen = false
        }, onRepeatGroup = {
            if(it != null) {
                // uiState의 repeatGroup 정보 업데이트
                viewModel.setRepeatGroup(it)
            }
        }, calendar, null)
    }
    //Todo uiState.RepeatGroup 정보 이용하여 repeatInfoText 값 변경
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetRepeatDialog(onDismiss: () -> Unit, onRepeatGroup: (RepeatGroup?) -> Unit, calendar: Calendar, repeatGroup: RepeatGroup?) {

    //SetRepeatDialog 종료시 ui에 맞는 repeatGroup 객체를 넘겨줌
    fun exitDialog() {
        if(repeatGroup == null) {
            //Todo repeatRadioGroup, durationRadioGroup, repeatInterval, endPlanDate, weeklyRule, monthlyRule 참조하여 repeatGroup 새로 만들기
            // repeatrule에서 last 입력 옵션은 일단 제거함
            //val newRepeatGroup = RepeatGroup()
            //onRepeatGroup(newRepeatGroup)
        } else {
            //Todo repeatRadioGroup, durationRadioGroup, repeatInterval, endPlanDate, weeklyRule, monthlyRule 참조하여 repeatGroup 업데이트
            onRepeatGroup(repeatGroup)
        }
        onDismiss()
    }
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
                //Todo 존재하던 repeatRule 반영 weeklyRule 값 변경 (해당하는 요일 index(0:월-6:일)의 값을 true로)
            }
        }
    }
    // repeatGroup table's repeatRule attribute value - month ver
    val monthlyRule = remember(repeatGroup) {
        mutableStateListOf(*Array(31) { false }).apply {
            if (repeatGroup==null) {
                val day = calendar.get(Calendar.DAY_OF_MONTH)-1
                this[day] = true
            } else {
                //Todo 존재하던 repeatRule 반영 monthlyRule 값 변경 (해당하는 (날짜-1)(0-30) index의 값을 true로)
            }
        }
    }
    // repeatGroup table's endDate attribute value
    var endPlanDate = remember(repeatGroup) {
        mutableStateOf<Date?>(null).apply {
            if (repeatGroup==null) calendar.time
            else repeatGroup.endDate
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

    Dialog(
        onDismissRequest = {exitDialog()}, properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn {
                item {
                    TopAppBar(modifier = Modifier.fillMaxWidth(), navigationIcon = {
                        IconButton(onClick = {exitDialog()}) {
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
                            text = "기간",
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
        Text(text = text)

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
            Text(text = text2)
        } else {
            Text(text = text)
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
            Text("반복할 요일 선택")
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
                        colors = if (buttonStates[index]) {
                            ButtonDefaults.textButtonColors(containerColor = Color.LightGray)
                        } else ButtonDefaults.textButtonColors(containerColor = Color.Transparent),
                        modifier = Modifier
                            .weight(1f)
                            .clip(CircleShape)
                            .padding(8.dp)
                    ) {
                        Text(text = day, modifier = if (buttonStates[index]) Modifier.drawBehind {
                            drawCircle(color = Color.Red, radius = this.size.maxDimension, style = Stroke(width = 4.dp.toPx()))
                        } else Modifier
                        )

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
            Text("반복할 날짜 선택")
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



@Composable
fun EndTimeRadioButtonLine(
    type: String, text: String, selected: MutableState<String>, endDate: MutableState<Date?>
) {
    Column(
        modifier = Modifier.animateContentSize(),
    ) {
        RadioButtonLine(type = type, text = text, selected = selected)
        AnimatedVisibility(visible = selected.value==type) {
            //Todo timepicker 구현 후  1. addplan의 경우 picker에서 선택한 값으로 endDate값 변경하기 2. editplan일 경우 endDate 값이 특정 값으로 주어질 텐데 그 값에 맞춰 picker 초기 상태 설정해주기
        }
    }
}

