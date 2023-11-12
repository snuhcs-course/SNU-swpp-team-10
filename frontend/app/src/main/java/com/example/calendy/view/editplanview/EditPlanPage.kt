package com.example.calendy.view.editplanview

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.calendy.data.dummy.DummyCategoryRepository
import com.example.calendy.data.dummy.DummyRepeatGroupRepository
import com.example.calendy.data.dummy.DummyScheduleRepository
import com.example.calendy.data.dummy.DummyTodoRepository
import com.example.calendy.data.maindb.category.Category
import com.example.calendy.data.maindb.category.ICategoryRepository
import com.example.calendy.data.maindb.plan.PlanType
import com.example.calendy.data.maindb.plan.Schedule
import com.example.calendy.data.maindb.plan.Todo
import com.example.calendy.data.maindb.plan.schedule.IScheduleRepository
import com.example.calendy.data.maindb.plan.todo.ITodoRepository
import com.example.calendy.data.maindb.repeatgroup.IRepeatGroupRepository
import com.example.calendy.data.maindb.repeatgroup.RepeatGroup
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarConfig
import kotlinx.coroutines.flow.Flow
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPlanPage(editPlanViewModel: EditPlanViewModel, onNavigateBack: () -> Unit) {
    val editPlanUiState: EditPlanUiState by editPlanViewModel.uiState.collectAsState()
    val categoryList: List<Category> by editPlanViewModel.categoryListState.collectAsState()

    val isPageSchedule = editPlanUiState.entryType==PlanType.SCHEDULE
    val isPageTodo = editPlanUiState.entryType==PlanType.TODO

    val isPageAdd = editPlanUiState.isAddPage
    val isPageEdit = !isPageAdd

    val verticalScrollState = rememberScrollState(initial = 0)
    Column(
        modifier = Modifier
            .verticalScroll(verticalScrollState)
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        /* TODO Should not show bottom navigation */
        //region Top Bar
        TopAppBar(
            showBackButton = true,
            onBackPressed = { onNavigateBack() },
            title = { },
            trailingContent = {
                Row {
                    if (isPageAdd) {
                        // Add Button
                        IconButton(onClick = {
                            editPlanViewModel.addPlan()
                            onNavigateBack()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Add, contentDescription = "Add",
                            )
                        }
                    }
                    if (isPageEdit) {
                        // Delete Button
                        IconButton(onClick = {
                            editPlanViewModel.deletePlan()
                            onNavigateBack()

                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                            )
                        }
                        // Save Button
                        IconButton(onClick = {
                            editPlanViewModel.updatePlan()
                            onNavigateBack()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = "Submit",
                            )
                        }
                    }
                }
            },
        )
        //endregion

        //region Type Buttons
        if (isPageAdd) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                listOf(PlanType.SCHEDULE, PlanType.TODO).forEach {
                    TypeButton(
                        text = when (it) {
                            PlanType.SCHEDULE -> "일정"
                            PlanType.TODO     -> "Todo"
                        }, isSelected = (editPlanUiState.entryType==it), onClick = {
                            editPlanViewModel.setType(it)
                        }, modifier = Modifier.background(
                            color = Color(0xFFDBE2F6), shape = RoundedCornerShape(size = 30.dp)
                        )

                    )
                }
            }
        }
        //endregion

        //region Title Text Field & Checkbox
        Row(verticalAlignment = Alignment.CenterVertically) {
            BasicTextField(value = editPlanUiState.titleField,

                           onValueChange = { value -> editPlanViewModel.setTitle(value) },
                           modifier = Modifier
                               .weight(1f)
                               .then(
                                   Modifier.padding(
                                       horizontal = 12.dp, vertical = 5.dp
                                   )
                               ), // Remove padding
                           textStyle = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold),
                           decorationBox = { innerTextField ->
                               if (editPlanUiState.titleField.isEmpty()) {
                                   Text(
                                       text = "제목",
                                       fontSize = 32.sp,
                                       fontWeight = FontWeight.Normal,
                                       color = Color.LightGray
                                   )
                               }
                               innerTextField()
                           })
        }
        //endregion

        //region Date Selector
        if (isPageTodo) {
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
        if (isPageSchedule) {
            DateRangeSelector(
                startTime = editPlanUiState.startTime,
                endTime = editPlanUiState.endTime,
                isAllDay = editPlanUiState.isAllDay,
                onSelectTimeRange = editPlanViewModel::setTimeRange,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(alignment = Alignment.CenterHorizontally)
            )
        }
        //endregion

        //region Repeat
        FieldWithLeadingText(leadingText = "반복") {
            SetRepeat(editPlanUiState, editPlanViewModel)
        }
        //endregion

        //region Category
        FieldWithLeadingText(leadingText = "분류") {
            CategorySelector(
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
                    config = RatingBarConfig().size(30.dp)
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
                               if (editPlanUiState.memoField.isEmpty()) {
                                   Text(
                                       text = "memo",
                                       fontSize = 16.sp,
                                       fontWeight = FontWeight.Normal,
                                       color = Color.LightGray
                                   )
                               }
                               innerTextField()
                           })
        }
        //endregion

        //region Show In Monthly View Switch
        FieldWithLeadingText(
            leadingText = "월별 캘린더에 표시", modifier = Modifier.fillMaxWidth(), textWidth = 240.dp
        ) {
            Checkbox(checked = editPlanUiState.showInMonthlyView,
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
fun TypeButton(
    text: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier
) {
    Button(
        onClick = { onClick() },
        colors = ButtonDefaults.buttonColors(if (isSelected) Color(0xFFDBE2F6) else Color.Transparent),
        contentPadding = PaddingValues(0.dp),
        modifier = modifier
            .border(
                width = 2.dp, color = Color(0xFF000000), shape = RoundedCornerShape(size = 30.dp)
            )
            .width(60.dp)
            .height(25.dp)
    ) {
        Text(
            text = text, fontSize = 16.sp, style = TextStyle(
                fontSize = 14.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight(500),
                color = Color(0xFF000000),

                textAlign = TextAlign.Center,
            )
        )
    }
}

// Description leading text on left
@Composable
fun FieldWithLeadingText(
    leadingText: String,
    alignment: Alignment.Vertical = Alignment.CenterVertically,
    modifier: Modifier = Modifier,
    textWidth: Dp = 80.dp,
    content: @Composable (RowScope.() -> Unit)
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

@Preview(showBackground = true, name = "Todo Screen Preview")
@Composable
fun TodoScreenPreview() {
    EditPlanPage(editPlanViewModel = EditPlanViewModel(
        scheduleRepository = DummyScheduleRepository(),
        todoRepository = DummyTodoRepository(),
        categoryRepository = DummyCategoryRepository(),
        repeatGroupRepository = DummyRepeatGroupRepository()
    ), onNavigateBack = { })
}
