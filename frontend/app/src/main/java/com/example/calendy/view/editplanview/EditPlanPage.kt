package com.example.calendy.view.editplanview

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.GestureCancellationException
import androidx.compose.foundation.gestures.PressGestureScope
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
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
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.consumeDownChange
import androidx.compose.ui.input.pointer.isOutOfBounds
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChangeConsumed
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calendy.data.dummy.DummyCategoryRepository
import com.example.calendy.data.dummy.DummyPlanRepository
import com.example.calendy.data.dummy.DummyRepeatGroupRepository
import com.example.calendy.data.maindb.category.Category
import com.example.calendy.data.maindb.plan.PlanType
import com.example.calendy.ui.theme.CalendyFontFamily
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarConfig
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPlanPage(editPlanViewModel: EditPlanViewModel, onNavigateBack: () -> Unit) {
    val editPlanUiState: EditPlanUiState by editPlanViewModel.uiState.collectAsState()
    val categoryList: List<Category> by editPlanViewModel.categoryListState.collectAsState()

    val isPageSchedule = editPlanUiState.entryType==PlanType.SCHEDULE
    val isPageTodo = editPlanUiState.entryType==PlanType.TODO

    val isPageAdd = editPlanUiState.isAddPage
    val isPageEdit = !isPageAdd

    val commonHeight =
        50.dp // common height for the Repeat, Category, Priority, Memo, MonthlyViewSwitch regions

    val verticalScrollState = rememberScrollState(initial = 0)
    var timePickerOpen by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .verticalScroll(verticalScrollState)
            .fillMaxSize()
            .padding(16.dp)
            .pointerInput(Unit) {
                detectTapAndPressUnconsumed(onTap = {
                    timePickerOpen = false
                })
            }, verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
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
                                imageVector = Icons.Default.Check, contentDescription = "Add",
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
                        }, modifier = Modifier.weight(1f)
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
                               ), // Remove paddingc
                           textStyle = TextStyle(fontFamily = CalendyFontFamily, fontSize = 32.sp, fontWeight = FontWeight.Bold),
                           decorationBox = { innerTextField ->
                               if (editPlanUiState.titleField.isEmpty()) {
                                   Text(
                                       text = "제목",
                                       fontSize = 32.sp,
                                       fontWeight = FontWeight.ExtraBold,
                                       color = Color.LightGray
                                   )
                               }
                               innerTextField()
                           })
        }
        //endregion

        //region Date Selector
        if (isPageTodo) {
            OneDateSelectorButton(
                time = editPlanUiState.dueTime,
                onSelectTime = editPlanViewModel::setDueTime,
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            )
        }
        if (isPageSchedule) {
            TwoDateSelectorButton(
                startTime = editPlanUiState.startTime,
                endTime = editPlanUiState.endTime,
                onSelectTimeRange = editPlanViewModel::setTimeRange,
                timePickerOpen = timePickerOpen,
                setTimePickerOpen = { timePickerOpen = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .align(alignment = Alignment.CenterHorizontally)
            )
        }
        //endregion

        //region Repeat
        FieldWithLeadingText(leadingText = "반복", modifier = Modifier.height(commonHeight)) {
            SetRepeat(editPlanUiState, editPlanViewModel)
        }
        //endregion

        //region Category
        FieldWithLeadingText(leadingText = "분류", modifier = Modifier.height(commonHeight)) {
            CategorySelector(
                currentCategory = editPlanUiState.category,
                categoryList = categoryList,
                onAddCategory = editPlanViewModel::addCategory,
                onSelectCategory = editPlanViewModel::setCategory,
                onUpdateCategory = editPlanViewModel::updateCategory,
                onDeleteCategory = editPlanViewModel::deleteCategory,
            )
        }
        //endregion

        //region Priority
        FieldWithLeadingText(leadingText = "중요도", modifier = Modifier.height(commonHeight)) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
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
        FieldWithLeadingText(
            leadingText = "메모", modifier = Modifier.height(commonHeight), alignment = Alignment.Top
        ) {
            BasicTextField(value = editPlanUiState.memoField,
                           onValueChange = { value -> editPlanViewModel.setMemo(value) },
                           modifier = Modifier.fillMaxWidth(),
                           textStyle = TextStyle(fontFamily = CalendyFontFamily,fontSize = 16.sp),
                           decorationBox = { innerTextField ->
                               if (editPlanUiState.memoField.isEmpty()) {
                                   Text(
                                       text = "메모",
                                       style = MaterialTheme.typography.bodyLarge,
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
            leadingText = "월별 캘린더에 표시",
            modifier = Modifier
                .fillMaxWidth()
                .height(commonHeight),
            textWidth = 240.dp
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
    val backgroundColor = if (isSelected) Color(0xFFDBE2F6) else Color.White
    val contentColor = if (isSelected) Color.Black else Color(0xFFB1B1B1)
    val borderColor = if (isSelected) Color.Black else Color(0xFFB1B1B1)

    Button(
        onClick = { onClick() },
        // Set the containerColor for the selected/unselected state
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor, contentColor = contentColor
        ),
        // Adjust padding as needed
        contentPadding = PaddingValues(0.dp),
        shape = RoundedCornerShape(40.dp),
        border = BorderStroke(2.dp, borderColor),
        modifier = modifier
            .height(48.dp)
            .fillMaxWidth(),
    ) {
        Text(
            text = text, fontFamily = CalendyFontFamily,fontSize = 16.sp, style = TextStyle(
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
            style = MaterialTheme.typography.titleMedium,
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
        planRepository = DummyPlanRepository(),
        categoryRepository = DummyCategoryRepository(),
        repeatGroupRepository = DummyRepeatGroupRepository()
    ), onNavigateBack = { })
}

// Copied from TapGestureDetector.kt in package androidx.compose.foundation.gestures
private val NoPressGesture: suspend PressGestureScope.(Offset) -> Unit = { }

private class PressGestureScopeImpl(density: Density) : PressGestureScope, Density by density {
    private var isReleased = false
    private var isCanceled = false
    private val mutex = Mutex(locked = false)

    /**
     * Called when a gesture has been canceled.
     */
    fun cancel() {
        isCanceled = true
        mutex.unlock()
    }

    /**
     * Called when all pointers are up.
     */
    fun release() {
        isReleased = true
        mutex.unlock()
    }

    /**
     * Called when a new gesture has started.
     */
    suspend fun reset() {
        mutex.lock()
        isReleased = false
        isCanceled = false
    }

    override suspend fun awaitRelease() {
        if (!tryAwaitRelease()) {
            throw GestureCancellationException("The press gesture was canceled.")
        }
    }

    override suspend fun tryAwaitRelease(): Boolean {
        if (!isReleased && !isCanceled) {
            mutex.lock()
            mutex.unlock()
        }
        return isReleased
    }
}

suspend fun PointerInputScope.detectTapAndPressUnconsumed(
    onPress: suspend PressGestureScope.(Offset) -> Unit = NoPressGesture,
    onTap: ((Offset) -> Unit)? = null
) {
    val pressScope = PressGestureScopeImpl(this)
    forEachGesture {
        coroutineScope {
            pressScope.reset()
            awaitPointerEventScope {

                val down = awaitFirstDown(requireUnconsumed = false).also { it.consumeDownChange() }

                if (onPress!==NoPressGesture) {
                    launch { pressScope.onPress(down.position) }
                }

                val up = waitForUpOrCancellationInitial()
                if (up==null) {
                    pressScope.cancel() // tap-up was canceled
                } else {
                    pressScope.release()
                    onTap?.invoke(up.position)
                }
            }
        }
    }
}

suspend fun AwaitPointerEventScope.waitForUpOrCancellationInitial(): PointerInputChange? {
    while (true) {
        val event = awaitPointerEvent(PointerEventPass.Initial)
        if (event.changes.all { it.changedToUp() }) {
            // All pointers are up
            return event.changes[0]
        }

        if (event.changes.any { it.consumed.downChange || it.isOutOfBounds(size) }) {
            return null // Canceled
        }

        // Check for cancel by position consumption. We can look on the Final pass of the
        // existing pointer event because it comes after the Main pass we checked above.
        val consumeCheck = awaitPointerEvent(PointerEventPass.Final)
        if (consumeCheck.changes.any { it.positionChangeConsumed() }) {
            return null
        }
    }
}