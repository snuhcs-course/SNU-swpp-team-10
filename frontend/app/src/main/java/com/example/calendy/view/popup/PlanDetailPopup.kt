package com.example.calendy.view.popup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.calendy.R
import com.example.calendy.data.plan.Plan
import com.example.calendy.data.plan.Schedule
import com.example.calendy.data.plan.Todo
import com.example.calendy.utils.equalDay
import com.example.calendy.utils.toAmPmString
import com.example.calendy.utils.toDateTimeString
import com.example.calendy.utils.toTimeString
import java.util.Date

@Composable
fun PlanDetailPopup(
    plan: Plan,
    header: @Composable ()->Unit = {},
    editButton: @Composable() (BoxScope.() -> Unit) = {},
    onDismissed:()->Unit={}
){
    Dialog(
        onDismissRequest = onDismissed,
//        properties = DialogProperties(usePlatformDefaultWidth = false)
    ){
        Box(modifier = Modifier
            .width(300.dp)
            .height(400.dp)
            .shadow(
                elevation = 4.dp,
                spotColor = Color(0x40000000),
                ambientColor = Color(0x40000000)
            )
            .background(color = Color(0xFFF1F5FB), shape = RoundedCornerShape(size = 20.dp))
            .padding(25.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ){
                header()
                Divider(
                    modifier = Modifier
                        .padding(horizontal = 0.dp, vertical = 8.dp)
                )

                //show times
                when(plan){
                    is Schedule -> {
                        val sameDay = plan.startTime.equalDay(plan.endTime)
                        var startTimeString =plan.startTime.toDateTimeString()
                        val endTimeString ="~ " +  plan.endTime.toDateTimeString()
                        if(sameDay)
                        {
                            val isSameAMPM = plan.startTime.toAmPmString().equals( plan.endTime.toAmPmString())
                            val ampmString = if(isSameAMPM) "" else plan.endTime.toAmPmString() + " "
                            startTimeString += " ~ " + ampmString + plan.endTime.toTimeString()
                        }
                        else
                            startTimeString ="  " + startTimeString
                                    Text(
                            text = startTimeString,
                            style = TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 18.sp,
                                fontWeight = FontWeight(500),
                            )
                        )
                        if(!sameDay) {
                            Text(
                                text = endTimeString,
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    lineHeight = 18.sp,
                                    fontWeight = FontWeight(500),
                                )
                            )
                        }
                    }
                    is Todo -> {
                        Text(
                            text = plan.dueTime.toDateTimeString(),
                            style = TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 18.sp,
                                fontWeight = FontWeight(500),
                            )
                        )
                    }
                }

                //memo
                Text(
                    text = if(plan.memo.isNullOrEmpty()) "메모 없음 ..." else plan.memo,
                    style = TextStyle(
                        fontSize = 15.sp,
                        lineHeight = 25.sp,
                        color = Color(0xFF737373),
                    ),
                    maxLines=5,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth()

                )
                //location

                //memo
                Text(
                    text = /*TODO*/ "위치 정보 없음 ...",
                    style = TextStyle(
                        fontSize = 15.sp,
                        lineHeight = 25.sp,
                        color = Color(0xFF737373),
                    ),
                    maxLines=5,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth()

                )
            }

            editButton()
        }
    }
}


@Composable
fun EditButton(
    plan : Plan,
    onNavigateToEditPage:(id: Int?, type: Plan.PlanType, date: Date?) -> Unit,
    onEditComplete:(Plan)->Unit={},
    modifier:Modifier = Modifier
){
    IconButton(
        onClick = 
            {
                // TODO :Can we make it more simple?
                when(plan){
                    is Schedule -> onNavigateToEditPage(plan.id, Plan.PlanType.Schedule, null)
                    is Todo -> onNavigateToEditPage(plan.id, Plan.PlanType.Todo, null)
                }
            },

        modifier = modifier
//                .padding(end = 10.dp)
            .wrapContentWidth()
            .wrapContentHeight(),
    ) {
        Icon(painter = painterResource(id = R.drawable.edit_plan_button),"edit")
    }

}


@Preview
@Composable
fun PlanDetailPopupPreview(){
    val p=Todo(0,"Todo", Date(),memo="MemoMemoMemoMemoMemoMemoMemo MemoMemoMemo Memo MemoMemo MemoMemoMemo Memo")
    PlanDetailPopup(
        plan=p,
        header= {
            PopupHeaderTitle(
                title="일정이름이름 일정이름",
            )
                },
        editButton = {
            EditButton(
                plan = p,
                onNavigateToEditPage = {_,_,_->},
                modifier=Modifier.align(Alignment.TopEnd)
            )
        }
    )
}