package com.example.calendy.view.messagepage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calendy.AppViewModelProvider
import com.example.calendy.data.message.Message
import com.example.calendy.data.plan.Plan
import com.example.calendy.data.plan.Todo
import com.example.calendy.view.popup.PlanDetailPopup
import com.example.calendy.view.popup.PlanListPopup
import java.util.Date

@Composable
fun MessageContentUser(
    messageLog: Message
){
    Text(
        text = messageLog.content,
        modifier = Modifier
            .wrapContentSize()
            .padding(10.dp)
    )
}

@Composable
fun MessageContentManager(
    messageLog: Message
){
    when(messageLog.hasLogPlan)
    {
        false -> MessageContentManagerDefault(messageLog)
        true  -> MessageContentManagerWithButton(messageLog)
    }
}

@Composable
fun MessageContentManagerDefault(
    messageLog: Message
){
    Text(
        text = messageLog.content,
        modifier = Modifier
            .wrapContentSize()
            .padding(10.dp)
    )
}
@Composable
fun MessageContentManagerWithButton(
    messageLog: Message,
    messageContentViewModel: MessagePlanLogViewModel = viewModel(factory = AppViewModelProvider.Factory)
){
    val logPlanList:List<Plan> by messageContentViewModel.modifiedPlans.collectAsState()

    var openListPopup : Boolean by remember { mutableStateOf(false)   }
    var openDetailPopup : Boolean by remember { mutableStateOf(false) }
    var selectedPlan : Plan by remember { mutableStateOf(Todo(-1,"", Date()))     }



    fun onButtonClick () {
        // TODO: make popup global scope
        openListPopup = true
        messageContentViewModel.onMessageSelected(messageLog)
    }
    Column(

    ){
        Text(
            text = messageLog.content,
            modifier = Modifier
                .wrapContentSize()
                .padding(10.dp)
        )
        // TODO: 위아래 padding 조절
        Button(
            onClick = ::onButtonClick,
            colors = ButtonDefaults.buttonColors(Color(0xFFF1F1F1)),
            contentPadding = PaddingValues(0.dp),
            modifier= Modifier
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
                fontSize = 12.sp,
                style = TextStyle(
                    fontSize = 12.sp,
                    lineHeight = 14.sp,
                    fontWeight = FontWeight(500),
                    color = Color(0xFF000000),

                    textAlign = TextAlign.Center,
                )
            )
        }
    }
    
    //open tod0 list popup
    if(openListPopup && !openDetailPopup)
    {
        PlanListPopup(
            planList = logPlanList,
            header = { Text(text = "일정 자세히 보기") },
            onDismissed = { openListPopup = false },
            onItemClick =
            { plan ->
                selectedPlan = plan
                openDetailPopup = true
            },
            onCheckboxClicked =
            { plan, checked ->

            },
        )
    }

    if(openDetailPopup)
    {
        PlanDetailPopup(
            plan = selectedPlan,
            header = { selectedPlan.title },
            onDismissed = { openDetailPopup = false },

        )
    }


}