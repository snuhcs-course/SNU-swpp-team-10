package com.example.calendy.view.messageview

import LoadingAnimation1
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calendy.AppViewModelProvider
import com.example.calendy.data.maindb.message.Message
import com.example.calendy.data.maindb.plan.Plan
import com.example.calendy.data.maindb.plan.Todo
import com.example.calendy.ui.theme.Blue1
import com.example.calendy.utils.toTimeString
import com.example.calendy.view.popup.PlanDetailPopup
import com.example.calendy.view.popup.PlanModifiedListPopup
import java.util.Date

@Composable
fun MessageContentUser(
    messageLog: Message
) {
    Text(
        text = messageLog.content, modifier = Modifier
            .wrapContentSize()
            .padding(10.dp),
        fontSize = 14.sp,

    )
}

@Composable
fun MessageContentManager(
    messageLog: Message
) {
//    when (messageLog.hasRevision) {
//        false -> MessageContentManagerDefault(messageLog)
//        true  -> MessageContentManagerWithButton(messageLog)
//    }
    // OnProgress...
    when (messageLog.hasRevision) {
        false -> when (messageLog.content == "AI_THINKING") {
            true -> MessageContentManagerThinking()
            false -> MessageContentManagerDefault(messageLog)
        }

        true -> MessageContentManagerWithButton(messageLog)
    }
}

@Composable
fun SentTime(
    messageLog: Message
){
    Text(
        text = messageLog.sentTime.toTimeString(),
        modifier = Modifier
            .wrapContentSize()
            .padding(vertical = 10.dp),
        fontSize = 10.sp,
        color = Color.Gray
    )
}

@Composable
fun MessageContentManagerDefault(
    messageLog: Message
) {
    Text(
        text = messageLog.content, modifier = Modifier
            .wrapContentSize()
            .padding(10.dp),
        fontSize = 14.sp,

    )
}
@Composable
fun MessageContentManagerThinking(){
    Box(
        modifier = Modifier
            .wrapContentWidth()
            .wrapContentHeight()
            .padding(5.dp),
        contentAlignment = Alignment.Center
    ) {
          //OnProgress...
        LoadingAnimation1(
            modifier=Modifier
                .width(90.dp)
                .height(30.dp),
            circleColor = Blue1,
            circleSize = 8.dp,
            spaceBetween = 8.dp,
            travelDistance = 10.dp
        )
//        Text(
//            text = "AI 매니저가 살펴보고 있어요...",
//            modifier = Modifier
//                .wrapContentSize()
//                .padding(10.dp),
//            fontSize = 14.sp,
//
//            )
    }
}
@Composable
fun MessageContentManagerWithButton(
    messageLog: Message,
    messageContentViewModel: MessagePlanLogViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
//    val logPlanList: List<Plan> by messageContentViewModel.modifiedPlans.collectAsState()
    val modifiedPlanItems: List<ModifiedPlanItem> by messageContentViewModel.modifiedPlanItems.collectAsState()

    var openListPopup: Boolean by remember { mutableStateOf(false) }


    fun onButtonClick() {
        // TODO: make popup global scope
        openListPopup = true
        messageContentViewModel.onMessageSelected(messageLog)
    }
    Column(
        modifier = Modifier
            .clickable { onButtonClick() },
//        horizontalAlignment = Alignment.End

    ) {
        Text(
            text = messageLog.content, modifier = Modifier
                .wrapContentSize()
                .padding(top = 10.dp, bottom = 5.dp, end = 10.dp, start = 10.dp),
            fontSize = 14.sp,
        )
        // TODO: 위아래 padding 조절
        Button(
            onClick = ::onButtonClick,
            colors = ButtonDefaults.buttonColors(Color(0xFFF1F1F1)),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier
                .padding(horizontal = 15.dp, vertical = 5.dp)
                .fillMaxWidth()
                .wrapContentHeight()
//                .background(color = Color(0xFFF1F1F1), shape = CircleShape)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "해당 일정 자세히 보기",
                color = Color.Black,
                modifier = Modifier.padding(0.dp),
                fontSize = 14.sp,
                style = TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 14.sp,
                    fontWeight = FontWeight(500),
                    color = Color(0xFF000000),

                    textAlign = TextAlign.Center,
                )
            )
        }
//        Text(
//            text = "자세히",
//            modifier = Modifier
//                .wrapContentHeight()
//                .padding(end = 10.dp, bottom = 5.dp),
//            fontSize = 12.sp,
//            color = Color.DarkGray,
//            textAlign = TextAlign.End,
//        )
    }

    //open tod0 list popup
    if (openListPopup) {

        PlanModifiedListPopup(
            headerMessage = "일정 변경 사항",
            modifiedPlanItems = modifiedPlanItems,
            onDismissed = { openListPopup = false },
            viewModel= messageContentViewModel
        )
    }

}

@Preview
@Composable
fun MessageContentManagerPreview() {
    MessageContentManager(
        Message(
            0,
            Date(),
            true,
            "AI_THINKING",
            false,
        )
    )
}