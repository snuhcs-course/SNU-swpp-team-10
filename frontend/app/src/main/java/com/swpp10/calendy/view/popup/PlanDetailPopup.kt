package com.swpp10.calendy.view.popup

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
import com.swpp10.calendy.R
import com.swpp10.calendy.data.maindb.plan.Plan
import com.swpp10.calendy.data.maindb.plan.PlanType
import com.swpp10.calendy.data.maindb.plan.Schedule
import com.swpp10.calendy.data.maindb.plan.Todo
import com.swpp10.calendy.ui.theme.Blue_White
import com.swpp10.calendy.ui.theme.Light_Gray
import com.swpp10.calendy.utils.equalDay
import com.swpp10.calendy.utils.getPriorityString
import com.swpp10.calendy.utils.toAmPmString
import com.swpp10.calendy.utils.toDateTimeString
import com.swpp10.calendy.utils.toTimeString
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
                elevation = 4.dp, spotColor = Color(0x40000000), ambientColor = Color(0x40000000)
            )
            .background(color = Blue_White, shape = RoundedCornerShape(size = 20.dp))
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


                // times
                when(plan){
                    is Schedule -> {
                        // TODO: use PlanHelper->getTimeInfo()
                        val sameDay = plan.startTime.equalDay(plan.endTime)
                        var startTimeString =plan.startTime.toDateTimeString()
                        val endTimeString = " ~ ${plan.endTime.toDateTimeString()}"
                        if(sameDay)
                        {
                            val isSameAMPM = plan.startTime.toAmPmString().equals( plan.endTime.toAmPmString())
                            val ampmString = if(isSameAMPM) "" else "${plan.endTime.toAmPmString()} "
                            startTimeString += " ~ " + ampmString + plan.endTime.toTimeString(hour12=true)
                        }
                        else
                            startTimeString = "  $startTimeString"
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
                    is Todo     -> {
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

                // memo
                BasicInfoText(if(plan.memo.isNullOrEmpty()) "메모 없음 ..." else plan.memo  )

                // complete
                if(plan is Todo)
                    BasicInfoText(if(plan.complete) "완료" else "미완료")

            }

            editButton()
        }
    }
}

@Composable
fun BasicInfoText(
    text: String=""
){
    Text(
        text = text,
        style = TextStyle(
            fontSize = 15.sp,
            lineHeight = 25.sp,
            color = Light_Gray,
        ),
        maxLines=5,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()

    )
}


@Composable
fun EditButton(
    plan : Plan,
    onNavigateToEditPage:(id: Int?, type: PlanType, date: Date?) -> Unit,
    onEditComplete:(Plan)->Unit={},
    modifier:Modifier = Modifier
){
    IconButton(
        onClick = 
            {
                // TODO :Can we make it more simple?
                when(plan){
                    is Schedule -> onNavigateToEditPage(plan.id, PlanType.SCHEDULE, null)
                    is Todo     -> onNavigateToEditPage(plan.id, PlanType.TODO, null)
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
    val p= Todo(0, "Todo", Date(), memo="MemoMemoMemoMemoMemoMemoMemo MemoMemoMemo Memo MemoMemo MemoMemoMemo Memo")
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