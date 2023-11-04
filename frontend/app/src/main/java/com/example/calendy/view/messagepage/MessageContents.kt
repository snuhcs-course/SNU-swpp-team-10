package com.example.calendy.view.messagepage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calendy.data.message.Message
import com.example.calendy.view.monthlyview.MonthlyDayPlanListPopupKT

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
//    MessageContentManagerDefault(messageLog)
    MessageContentManagerWithButton(messageLog)
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
    messageLog: Message
){
    var openPopup : Boolean by remember { mutableStateOf(false)   }

    fun onButtonClick () {
        // TODO: make popup global scope
        openPopup = true
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
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            modifier= Modifier
                .padding(horizontal = 15.dp, vertical = 5.dp)
                .wrapContentWidth()
                .wrapContentHeight()
                .background(color = Color(0xFFF1F1F1), shape = CircleShape),
            contentPadding = PaddingValues(horizontal=12.dp,vertical=0.dp)
        ) {
            Text(
                text = "해당 일정 자세히 보기",
                color = Color.Black,
                modifier = Modifier.padding(0.dp),
                fontSize = 12.sp,
                lineHeight = 12.sp
            )
        }
    }

    if(openPopup){
    }
}