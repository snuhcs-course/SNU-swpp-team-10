package com.example.calendy.view.messagepage

import android.os.Build
import androidx.compose.foundation.lazy.items
import android.widget.CompoundButton.OnCheckedChangeListener
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calendy.AppViewModelProvider
import com.example.calendy.R
import com.example.calendy.data.plan.Plan
import com.example.calendy.data.plan.Schedule
import com.example.calendy.data.plan.Todo
import java.util.Date

@Composable
fun PlanListPopup(
    planList : List<Plan> = emptyList(),
    onItemClick: (Plan) -> Unit = {},
    header : @Composable ()->Unit = {}
){
    Dialog(
        onDismissRequest = { /*TODO*/ }
    ) {
        Column(
            modifier = Modifier
                .width(300.dp)
                .height(400.dp)
                .shadow(elevation = 4.dp, spotColor = Color(0x40000000), ambientColor = Color(0x40000000))
                .background(color = Color(0xFFF1F5FB), shape = RoundedCornerShape(size = 20.dp))
                .padding(25.dp)
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
                items(planList) {
                    when (it) {
                        is Schedule -> ScheduleListItem(schedule = it,onItemClick)
                        is Todo -> TodoListItem(todo = it,onItemClick)
                    }
                }
            }
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
    onChecked : (Boolean)->Unit = {}
){
    Row(
        modifier= Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = todo.complete!!,
            onCheckedChange = onChecked,
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




@Preview
@Composable
fun ListPopupPreview(){
    var planList : ArrayList<Plan> = ArrayList()
    planList.add(Schedule(0,"my schedule",Date(), Date()))
    planList.add(Todo(0,"my schedule",Date(),complete=false))
    PlanListPopup(planList){
        PopupHeaderTitle("Added plans")
    }
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

