package com.example.calendy.view.popup

import android.widget.Toast
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.calendy.data.maindb.plan.Plan
import com.example.calendy.data.maindb.plan.Schedule
import com.example.calendy.data.maindb.plan.Todo
import com.example.calendy.ui.theme.Light_Gray
import com.example.calendy.ui.theme.Light_Green
import com.example.calendy.ui.theme.getColor
import com.example.calendy.utils.dayOfWeek
import com.example.calendy.utils.getInfoText
import com.example.calendy.view.messageview.MessagePlanLogViewModel
import com.example.calendy.view.messageview.ModifiedPlanItem
import com.example.calendy.view.messageview.QueryType
import java.util.Date

@Composable
fun PlanRevisionListPopup(
    planPairList: List<Pair<Plan?, Plan?>> = emptyList(),
    header: @Composable ()->Unit = {},
    addButton:  @Composable() (BoxScope.() -> Unit)={},
    onItemClick: (Plan) -> Unit = {},
    onCheckboxClicked:(Plan, Boolean) -> Unit ={ plan, check->},
    onDismissed:()->Unit={}
){
    Dialog(
        onDismissRequest =  onDismissed
    ) {
        ListPopupBox(
            header=header,
            addButton=addButton,
            content = {
                items(planPairList) {
                    Column(modifier = Modifier.border(1.dp, Color(0xFF000000))) {
                        val (savedPlan, currentPlan) = it
                        if (savedPlan != null) {
                            when(savedPlan) {
                                is Schedule -> ScheduleListItem(schedule = savedPlan, onItemClick)
                                is Todo     -> TodoListItem(
                                    todo = savedPlan,
                                    onItemClick = onItemClick,
                                    onChecked = onCheckboxClicked
                                )
                            }
                        } else {
                            Text("New Plan Inserted")
                        }

                        if (currentPlan != null) {
                            when (currentPlan) {
                                is Schedule -> ScheduleListItem(schedule = currentPlan, onItemClick)
                                is Todo     -> TodoListItem(
                                    todo = currentPlan,
                                    onItemClick = onItemClick,
                                    onChecked = onCheckboxClicked
                                )
                            }
                        } else {
                            Text("This Plan is currently deleted")
                        }
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanModifiedListPopup(
    headerMessage:String = "Modified Plans",
    modifiedPlanItems: List<ModifiedPlanItem> = emptyList(),
    onDismissed:()->Unit={},
    viewModel:MessagePlanLogViewModel
){
    val context = LocalContext.current
    var recomposeTrigger by remember { mutableStateOf(1) }
    val showUndoAllButton = modifiedPlanItems.isNotEmpty() && modifiedPlanItems.any { !(it.queryType == QueryType.SELECT || !it.isValid) }
    fun forceRecompose(){
        recomposeTrigger = -recomposeTrigger
    }

    key(recomposeTrigger) {
        Dialog(
            onDismissRequest = onDismissed
        ) {
            ListPopupBox(
                header = { PopupHeaderTitle(headerMessage) },
                addButton = {
                    if(showUndoAllButton){
                        UndoButton(
                            modifier = Modifier
                                .align(Alignment.BottomEnd),
                            onUndoClick = {
                                viewModel.undoAllModify(modifiedPlanItems)
                                onDismissed()
                                Toast.makeText(context, "변경 사항을 모두 되돌렸습니다.", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            ) {
                items(modifiedPlanItems) {
//                    if(it.isValid)
                        PlanModifiedItem(
                            it,
                            openEditPlan = {},
                            undoModify =
                            { modifiedPlanItem ->
                                viewModel.undoModify(modifiedPlanItem)
                            },
                            forceRecompose = ::forceRecompose
                        )
                }
                if (modifiedPlanItems.isEmpty()) items(1) {
                    Text(
                        text = "모든 변경 사항을 되돌렸어요!",
                        style = TextStyle(
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = Light_Gray,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
fun SwitchablePlanListPopup(
    planList: List<Plan>? = emptyList(),
    header: @Composable ()->Unit = {},
    addButton:  @Composable() (BoxScope.() -> Unit),
    onItemClick: (Plan) -> Unit = {},
    onCheckboxClicked:(Plan, Boolean) -> Unit ={ plan, check->},
    onDismissed:()->Unit={},
    onLeftButton:()->Unit={},
    onRightButton:()->Unit={},
) {
    Dialog(
        onDismissRequest = onDismissed,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.wrapContentWidth()
        ) {

            IconButton(
                onClick = onLeftButton,
                modifier = Modifier
                    .padding(5.dp)
                    .weight(1f)
                    .height(400.dp) //hard coded equal to popup box height
                    .requiredWidthIn(min = 50.dp, max = 60.dp)
            ) {
                Icon(
                    painter = painterResource(id = com.prolificinteractive.materialcalendarview.R.drawable.mcv_action_previous),
                    contentDescription = "prev",
                    tint=Color.DarkGray,
                    modifier = Modifier
                        .size(50.dp)

                )
            }
            ListPopupBox(
                header = header,
                addButton = addButton,
                content = {
                    items(planList!!) {
                        when (it) {
                            is Schedule -> ScheduleListItem(schedule = it, onItemClick)
                            is Todo     -> TodoListItem(
                                todo = it,
                                onItemClick = onItemClick,
                                onChecked = onCheckboxClicked
                            )
                        }
                    }
                }
            )
            IconButton(
                onClick = onRightButton,
                modifier = Modifier
                    .padding(5.dp)
                    .weight(1f)
                    .height(400.dp) //hard coded equal to popup box height
                    .requiredWidthIn(min = 50.dp, max = 60.dp)
            ) {
                Icon(
                    painter = painterResource(id = com.prolificinteractive.materialcalendarview.R.drawable.mcv_action_next),
                    contentDescription = "next",
                    tint=Color.DarkGray,
                    modifier = Modifier
                        .size(50.dp)

                )
            }
        }
    }

}


@Composable
fun ListPopupBox(
    header: @Composable ()->Unit = {},
    addButton:  @Composable() (BoxScope.() -> Unit),
    content: LazyListScope.() -> Unit
){
    Box(modifier = Modifier
        .width(300.dp)
        .height(400.dp)
        .shadow(
            elevation = 4.dp, spotColor = Color(0x40000000), ambientColor = Color(0x40000000)
        )
        .background(color = Color(0xFFF1F5FB), shape = RoundedCornerShape(size = 20.dp))
        .padding(25.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {

            header()
            Divider(
                modifier = Modifier
                    .padding(horizontal = 0.dp, vertical = 8.dp)
            )
            LazyColumn(
                modifier = Modifier
//                    .padding(start = 10.dp, end = 20.dp)
                    .fillMaxWidth()
            ) {
                this.content()
            }
        }


        //add button
        addButton()
    }
}

@Composable
fun PopupHeaderTitle(
    title:String ="",
    modifier:Modifier = Modifier
){
    Text(
        text = title,
        style = TextStyle(
            fontSize = 28.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight(700),
            color = Color(0xFF000000),
            letterSpacing = 0.37.sp,
        ),
        maxLines=1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
    )
}
@Composable
fun PopupHeaderDate(
    date:Date = Date(2023,11,31)
){
    Row(
        verticalAlignment = Alignment.Bottom
    ){
        Text(
            text = date.date.toString(),
            modifier = Modifier
                .padding(end=10.dp),
            style = TextStyle(
                fontSize = 34.sp,
                lineHeight = 41.sp,
                fontWeight = FontWeight(700),
                color = Color(0xFF000000),
                letterSpacing = 0.37.sp,
            )
        )
        Text(
            text = date.dayOfWeek(),
            style = TextStyle(
                fontSize = 16.sp,
                lineHeight = 41.sp,
                fontWeight = FontWeight(700),
                color = Color(0xFF000000),
                letterSpacing = 0.37.sp,
            )
        )
    }
}

@Composable
fun ScheduleListItem(
    schedule : Schedule,
    onItemClick: (Plan) -> Unit = {}
){
    Row(
        modifier= Modifier
            .padding(vertical = 5.dp, horizontal = 0.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = 5.dp, horizontal = 0.dp)
                .width(15.dp)
                .height(15.dp)
                .background(color = schedule.getColor(), shape = CircleShape)
        )
        Column(
            modifier= Modifier
                .fillMaxWidth()
                .clickable { onItemClick(schedule) }
        ) {
            Text(
                text = schedule.title,
                maxLines = 1,
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 18.sp,
                    color = Color(0xFF000000),
                    ),
                modifier = Modifier
                    .padding(horizontal = 10.dp),
            )
            Text(
                text = schedule.getInfoText(),
                maxLines = 1,
                style = TextStyle(
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                    color = Color(0xFF646464),
                    ),
                modifier = Modifier
                    .padding(horizontal = 10.dp),
            )
        }
    }
}

@Composable
fun TodoListItem(
    todo : Todo,
    onItemClick: (Plan) -> Unit ={},
    onChecked : (Plan, Boolean) -> Unit = { plan, check->}
){
    Row(
        modifier= Modifier
            .padding(vertical = 5.dp, horizontal = 0.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Checkbox(
            checked = todo.complete!!,
            onCheckedChange = {check -> onChecked(todo,check)},
            modifier = Modifier
                .padding(vertical = 5.dp, horizontal = 0.dp)
                .width(15.dp)
                .height(15.dp)
                .scale(0.8f),
            colors = CheckboxDefaults.colors(
                checkedColor = todo.getColor(),
                uncheckedColor = todo.getColor(),
                checkmarkColor = Color.White
            )
        )
        Column(
            modifier= Modifier
                .fillMaxWidth()
                .clickable(onClick = { onItemClick(todo) })

        ) {
            Text(
                text = todo.title,
                maxLines = 1,
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 18.sp,
                    color = if (todo.complete!!) Color(0xFF646464) else Color(0xFF000000),
                    textDecoration = if (todo.complete!!) TextDecoration.LineThrough else TextDecoration.None,
                ),
                modifier = Modifier
                    .padding(horizontal = 10.dp),
            )
            val todoText=todo.getInfoText()
            if(todoText.isNotEmpty())
                Text(
                    text = todoText,
                    maxLines = 1,
                    style = TextStyle(
                        fontSize = 12.sp,
                        lineHeight = 12.sp,
                        color = Color(0xFF646464),
                        textDecoration = if (todo.complete!!) TextDecoration.LineThrough else TextDecoration.None,
                    ),
                    modifier = Modifier
                        .padding(horizontal = 10.dp),
            )
        }
    }
}

enum class ModifiedTextType {
    ADD, DELETE, SHOW
}
@Composable
fun PlanModifiedItem(
    modifiedPlanItem: ModifiedPlanItem,
    openEditPlan: (Plan) -> Unit ={},
    undoModify: (ModifiedPlanItem) -> Unit ={},
    forceRecompose : ()->Unit = {} // to force recompose of parent
) {
    val titlePlan:Plan = modifiedPlanItem.titlePlan
    val modifiedBeforeText: String = modifiedPlanItem.beforeText
    val modifiedAfterText: String = modifiedPlanItem.afterText
    val type = modifiedPlanItem.queryType

    val context = LocalContext.current

    var expandMenu :Boolean by remember {mutableStateOf(false)}
    var openDetail :Boolean by remember {mutableStateOf(false)} // detail popup for logged plan
    var detailPlan :Plan by remember {mutableStateOf(titlePlan)}
    fun openDetailOf(plan:Plan){
        detailPlan = plan
        openDetail = true
    }

    Column(
        modifier = when(modifiedPlanItem.isValid){
            false -> Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp, horizontal = 0.dp)
            true -> Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp, horizontal = 0.dp)
                .clickable { expandMenu = true }
        }
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            // color of circle set to plan after
            when(titlePlan){
                is Schedule -> Box(
                    modifier = Modifier
                        .padding(vertical = 5.dp, horizontal = 0.dp)
                        .width(15.dp)
                        .height(15.dp)
                        .background(color = titlePlan.getColor(), shape = CircleShape)
                )
                is Todo -> Checkbox(
                    checked = titlePlan.complete!!,
                    onCheckedChange = {},
                    enabled=false,
                    modifier = Modifier
                        .padding(vertical = 5.dp, horizontal = 0.dp)
                        .width(15.dp)
                        .height(15.dp)
                        .scale(0.8f),
                    colors = CheckboxDefaults.colors(
                        disabledCheckedColor = titlePlan.getColor(),
                        disabledUncheckedColor = titlePlan.getColor(),
                        checkmarkColor = Color.White
                    )
                )
            }
            // title set to plan after
            Text(
                text = titlePlan.title,
                maxLines = 1,
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 18.sp,
                    color = Color(0xFF000000),
                ),
                modifier = Modifier.padding(horizontal = 10.dp),
            )
        }

        Column(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
            verticalArrangement = Arrangement.spacedBy(0.dp),

        ) {
            if(!modifiedBeforeText.isNullOrEmpty())
                ModifiedText(ModifiedTextType.DELETE, modifiedBeforeText)
            if(!modifiedAfterText.isNullOrEmpty())
                if(modifiedPlanItem.queryType == QueryType.SELECT)
                    ModifiedText(ModifiedTextType.SHOW, modifiedAfterText)
                else
                    ModifiedText(ModifiedTextType.ADD, modifiedAfterText)
        }
    }


    DropdownMenu(
        //TODO : connect edit plan page
        expanded = expandMenu,
        onDismissRequest = { expandMenu=false }) {
        when(type){
            QueryType.INSERT     ->
                DropdownMenuItem(text = { Text(text="추가된 일정 보기") }, onClick = { openDetailOf(modifiedPlanItem.planAfter!!) })
            QueryType.UPDATE     ->
            {
                DropdownMenuItem(text = { Text(text="수정 전 일정 보기") }, onClick = { openDetailOf(modifiedPlanItem.planBefore!!) })
                DropdownMenuItem(text = { Text(text="수정 후 일정 보기") }, onClick = { openDetailOf(modifiedPlanItem.planAfter!!) })
            }
            QueryType.DELETE     ->
                DropdownMenuItem(text = { Text(text="삭제된 일정 보기") }, onClick = { openDetailOf(modifiedPlanItem.planBefore!!)})
            QueryType.SELECT     -> 
                DropdownMenuItem(text = { Text(text="발견한 일정 보기") }, onClick = { openDetailOf(modifiedPlanItem.planAfter!!)})
            else                 -> {}
        }

        if(type != QueryType.SELECT)
            DropdownMenuItem(
                text = { Text(text="되돌리기") },
                onClick = {
                    undoModify(modifiedPlanItem)
                    expandMenu=false
                    modifiedPlanItem.isValid=false
                    Toast.makeText(context, "변경 사항을 되돌렸습니다.", Toast.LENGTH_SHORT).show()
                    forceRecompose()
                })
    }

    if(openDetail){
        PlanDetailPopup(
            plan = detailPlan,
            header = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    PopupHeaderTitle(detailPlan.title)

                    when(detailPlan){
                        is Schedule -> Box(
                            modifier = Modifier
                                .padding(vertical = 5.dp, horizontal = 0.dp)
                                .width(25.dp)
                                .height(25.dp)
                                .background(color = detailPlan.getColor(), shape = CircleShape)
                        )
                        is Todo -> Checkbox(
                            checked = (detailPlan as Todo).complete!!,
                            onCheckedChange = {},
                            enabled=false,
                            modifier = Modifier
                                .padding(vertical = 5.dp, horizontal = 0.dp)
                                .width(25.dp)
                                .height(25.dp)
                                .scale(0.8f),
                            colors = CheckboxDefaults.colors(
                                disabledCheckedColor = detailPlan.getColor(),
                                disabledUncheckedColor = detailPlan.getColor(),
                                checkmarkColor = Color.White
                            )
                        )
                    }
                }
             },
            onDismissed = {openDetail=false}
        )
    }
}


@Composable
private fun ModifiedText(
    type: ModifiedTextType,
    
    modifiedBeforeText: String
) {
    val label = when (type) {
        ModifiedTextType.ADD -> "+"
        ModifiedTextType.DELETE -> "–"
        ModifiedTextType.SHOW -> ""
    }

    val textColor = when (type) {
        ModifiedTextType.ADD -> Light_Green
        ModifiedTextType.DELETE -> Light_Gray
        ModifiedTextType.SHOW -> Color(0xFF000000)
    }


    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {

        // before
        Text(
            text = label,
            maxLines = 1,
            style = TextStyle(
                fontSize = 12.sp,
                lineHeight = 12.sp,
                color = textColor,
                textAlign = TextAlign.End,
            ),
            modifier = Modifier.width(20.dp),
        )
        Text(
            text = modifiedBeforeText,
            maxLines = 3,
            style = TextStyle(
                fontSize = 12.sp,
                lineHeight = 12.sp,
                color = textColor,
                textDecoration = if (type == ModifiedTextType.DELETE) TextDecoration.LineThrough else TextDecoration.None,
            ),
            modifier = Modifier.padding(end = 10.dp),
        )
    }
}


@Composable
fun AddButton(
    onButtonClick: () -> Unit = {},
    modifier: Modifier = Modifier
){

    FloatingActionButton(
        onClick = onButtonClick,
        modifier = modifier
//                .padding(end = 10.dp)
            .wrapContentWidth()
            .wrapContentHeight()

    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription =  "add",
        )

    }
}

@Composable
fun UndoButton(
    modifier: Modifier,
    onUndoClick: () -> Unit = {},
){
    var alertDialogOpen by remember { mutableStateOf(false) }
    FloatingActionButton(
        onClick = { alertDialogOpen = true },
        modifier = modifier
            .wrapContentWidth()
            .wrapContentHeight()
    ) {
        Icon(
            imageVector = Icons.Filled.Undo,
            contentDescription =  "undo",
        )
    }

    if (alertDialogOpen)
        AlertDialog(
            onDismissRequest = { alertDialogOpen = false },
            title = {
                Text(
                    text = "변경 사항을 모두 되돌리시겠습니까?",
                    style = TextStyle(
                        fontSize = 14.sp,
                        lineHeight = 16.sp,
                        color = Color(0xFF000000),
                    ),
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        alertDialogOpen = false
                        onUndoClick()
                    }
                )
                { Text("확인") }
            },
            dismissButton = {
                TextButton(onClick = {alertDialogOpen = false}) {Text("취소")}
            },
            modifier = Modifier
                .padding(10.dp)
                .background(Color.White, shape = RoundedCornerShape(10.dp)),
            shape = RoundedCornerShape(10.dp),
        )
}

@Preview(showBackground = true)
@Composable
fun PlanRevisionListPreview() {
    val planPairList : MutableList<Pair<Plan?, Plan?>> = mutableListOf()
    planPairList.add(Pair(Schedule(
        id = 0,
        title = "my schedule Before Revision",
        startTime = Date(),
        endTime = Date()
    ), Schedule(id = 0, title = "my schedule Currently", startTime = Date(), endTime = Date())))
    planPairList.add(Pair(Todo(id = 0, title = "my Todo Before Revision", dueTime = Date(), complete=false),
                          Todo(id = 0, title = "my Todo Currently", dueTime = Date(), complete=false)))

    PlanRevisionListPopup(
        planPairList,
        header = { PopupHeaderTitle("Added plans") },
        addButton = {
            AddButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
            )
        }
    )
}

@Preview
@Preview(showBackground = true)
@Composable
fun ListPopupPreview(){
    var planList : ArrayList<Plan> = ArrayList()
    planList.add(Schedule(0, "my schedule", Date(), Date()))
    planList.add(Todo(0, "my schedule", Date(), complete=false))
    SwitchablePlanListPopup(
        planList,
        header = { PopupHeaderTitle("Added plans") },
        addButton = {
            AddButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)

            )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun ScheduleListPreview(){
    ScheduleListItem(schedule = Schedule(0,"my schedule",Date(), Date()))
}
@Preview(showBackground = true)
@Composable
fun TodoListPreview(){
    TodoListItem(todo = Todo(0,"my schedule",Date(),complete=false))
}
@Preview(showBackground = true)
@Composable
fun ModifiedListPreview(){

    Column{
        
        PlanModifiedItem(
            ModifiedPlanItem(
                historyId=-1,
                planBefore = Schedule(0,"my schedule",Date(2023,10,12,2,0), Date(2023,10,12,4,0)),
                planAfter = Schedule(0,"me schedule",Date(2023,10,14,2,0), Date(2023,10,14,4,0)),
                queryType = QueryType.UPDATE
            ),
            openEditPlan={}
        )
        PlanModifiedItem(
            ModifiedPlanItem(
                historyId=-1,
                planBefore = Todo(0,"my todo",Date(2023,10,12,2,0),false),
                planAfter = Todo(0,"me todo",Date(2023,10,14,2,0), true),
                queryType = QueryType.UPDATE
            ),
            openEditPlan={}
        )
    }
}
@Preview(showBackground = true)
@Composable
fun PopupHeaderPreview(){
    PopupHeaderTitle("Added Plans")
}
@Preview(showBackground = true)
@Composable
fun PopupHeaderDatePreview(){
    PopupHeaderDate()
}
@Preview(showBackground = true)
@Composable
fun EditButtonPreview(){
    EditButton(Todo(0, "Todo", Date()), { id, plan, date->})
}
@Preview(showBackground = true)
@Composable
fun AddButtonPreview(){
    AddButton( )
}