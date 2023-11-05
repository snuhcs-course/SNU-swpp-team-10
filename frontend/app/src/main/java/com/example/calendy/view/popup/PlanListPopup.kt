package com.example.calendy.view.popup

import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.calendy.R
import com.example.calendy.data.plan.Plan
import com.example.calendy.data.plan.Schedule
import com.example.calendy.data.plan.Todo
import com.example.calendy.utils.dayOfWeek
import java.util.Date

@Composable
fun PlanListPopup(
    planList: List<Plan>? = emptyList(),
    header: @Composable ()->Unit = {},
    addButton:  @Composable() (BoxScope.() -> Unit),
    onItemClick: (Plan) -> Unit = {},
    onCheckboxClicked:(Plan, Boolean) -> Unit ={ plan, check->},
    onDismissed:()->Unit={}
){
    Dialog(
        onDismissRequest = { onDismissed }
    ) {
        Box(modifier = Modifier
            .width(300.dp)
            .height(400.dp)
            .shadow(
                elevation = 4.dp,
                spotColor = Color(0x40000000),
                ambientColor = Color(0x40000000)
            )
            .background(color = Color(0xFFF1F5FB), shape = RoundedCornerShape(size = 20.dp))
            .padding(25.dp))
        {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {

                header()
//            Divider(
//                modifier = Modifier
//                    .padding(horizontal = 0.dp, vertical = 2.dp)
//            )
                LazyColumn(
                    modifier = Modifier
                        .padding(vertical = 20.dp)
                        .fillMaxWidth()
                ) {
                    items(planList!!) {
                        when (it) {
                            is Schedule -> ScheduleListItem(schedule = it, onItemClick)
                            is Todo -> TodoListItem(
                                todo = it,
                                onItemClick = onItemClick,
                                onChecked = onCheckboxClicked
                            )
                        }
                    }
                }
            }


            //add button
            addButton()
        }
    }
}

@Composable
fun PopupHeaderTitle(
    title:String =""
){
    Text(
        text = title,
        style = TextStyle(
            fontSize = 34.sp,
            lineHeight = 41.sp,
            fontWeight = FontWeight(700),
            color = Color(0xFF000000),
            letterSpacing = 0.37.sp,
        )
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
            text = date.day.toString(),
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
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = 5.dp, horizontal = 0.dp)
                .width(15.dp)
                .height(15.dp)
                .background(color = Color(0xFFACC7FA), shape = CircleShape)
        )
        ClickableText(
            text = AnnotatedString(schedule.title),
            modifier = Modifier
                .padding(horizontal = 10.dp),
            onClick = {onItemClick(schedule)}
        )
    }
}

@Composable
fun TodoListItem(
    todo : Todo,
    onItemClick: (Plan) -> Unit ={},
    onChecked : (Plan, Boolean) -> Unit = {plan,check->}
){
    Row(
        modifier= Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = todo.complete!!,
            onCheckedChange = {check -> onChecked(todo,check)},
            modifier = Modifier
                .padding(vertical = 5.dp, horizontal = 0.dp)
                .width(15.dp)
                .height(15.dp)
                .scale(0.8f)
                .background(color = Color(0xFFACC7FA), shape = CircleShape)
        )
        ClickableText(
            text = AnnotatedString(todo.title),
            modifier = Modifier
                .padding(horizontal = 10.dp),
            onClick = {onItemClick(todo)}

        )
    }
}

@Composable
fun EditButton(
    plan : Plan,
    onNavigateToEditPage:(id: Int?, type: Plan.PlanType) -> Unit,
    onEditComplete:(Plan)->Unit={}
){
    IconButton(
        onClick = { onNavigateToEditPage },

        modifier = Modifier
//                .padding(end = 10.dp)
            .wrapContentWidth()
            .wrapContentHeight(),
    ) {
        Icon(painter = painterResource(id = R.drawable.edit_plan_button),"edit")
    }

}

@Composable
fun AddButton(
    date:Date=Date(),
    onNavigateToEditPage: (id: Int?, type: Plan.PlanType) -> Unit,
    onEditComplete:(Plan)->Unit={},
    modifier: Modifier = Modifier
){

    IconButton(
        onClick = { onNavigateToEditPage },

        modifier = modifier
//                .padding(end = 10.dp)
            .wrapContentWidth()
            .wrapContentHeight()

    ) {
        Icon(
            painter = painterResource(id = R.drawable.add_new_plan_button),
            contentDescription =  "add",
            modifier = Modifier
                .wrapContentSize()

        )
    }
}


@Preview
@Composable
fun ListPopupPreview(){
    var planList : ArrayList<Plan> = ArrayList()
    planList.add(Schedule(0,"my schedule",Date(), Date()))
    planList.add(Todo(0,"my schedule",Date(),complete=false))
    PlanListPopup(
        planList,
        header = { PopupHeaderTitle("Added plans") },
        addButton = {
            AddButton(
                onNavigateToEditPage = { id, plan -> },
                onEditComplete = {},
                modifier = Modifier
                    .align(Alignment.BottomEnd)

            )
        }
    )
}

@Preview
@Composable
fun ScheduleListPreview(){
    ScheduleListItem(schedule = Schedule(0,"my schedule",Date(), Date()))
}
@Preview
@Composable
fun TodoListPreview(){
    TodoListItem(todo = Todo(0,"my schedule",Date(),complete=false))
}
@Preview
@Composable
fun PopupHeaderPreview(){
    PopupHeaderTitle("Added Plans")
}
@Preview
@Composable
fun PopupHeaderDatePreview(){
    PopupHeaderDate()
}
@Preview
@Composable
fun EditButtonPreview(){
    EditButton(Todo(0,"Todo",Date()),{id,plan->})
}
@Preview
@Composable
fun AddButtonPreview(){
    AddButton(onNavigateToEditPage = { id, plan->}, onEditComplete = {})
}