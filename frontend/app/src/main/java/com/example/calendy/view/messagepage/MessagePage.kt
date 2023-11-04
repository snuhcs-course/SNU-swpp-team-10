package com.example.calendy.view.messagepage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calendy.AppViewModelProvider
import com.example.calendy.R
import com.example.calendy.data.message.Message
import com.example.calendy.utils.equalDay
import com.example.calendy.utils.toDayString
import java.util.Date

@Composable
fun MessagePage(
    messagePageViewModel: MessagePageViewModel = viewModel(factory = AppViewModelProvider.Factory),
    sqlExecutionViewModel: SqlExecutionViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val messageUiState:MessageUIState by messagePageViewModel.uiState.collectAsState()
    val userInput = messageUiState.userInputText
    val messageLogList : List<Message> = messageUiState.messageLogs

    fun onValueChanged(text:String){
        messagePageViewModel.setUserInputText(text)
    }
    fun onSendButtonClicked(){
        messagePageViewModel.addUserMessage()
        messagePageViewModel.setUserInputText("")
//        sqlExecutionViewModel.sendQuery(userInput)

    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ){

    MessageList(messageLogList, modifier = Modifier
        .weight(1f)
        .fillMaxHeight())
    MessageInputField(
        onValueChanged = ::onValueChanged,
        onSendButtonClicked = ::onSendButtonClicked,
        text = userInput
    )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageInputField(
    onSendButtonClicked: ()->Unit = {},
    onValueChanged: (text:String)->Unit = {},
    text: String = "",
    modifier :Modifier = Modifier
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .background(color = Color(0xFFF1F1F1))
    ) {
        BasicTextField(
            value = text,
            onValueChange = onValueChanged,
            modifier = modifier
                .padding(top = 10.dp, bottom = 10.dp, start = 10.dp)
                .fillMaxWidth()
                .weight(1f)
                .background(color = Color(0xFFFFFEFE), shape = RoundedCornerShape(size = 25.dp))
                .then(Modifier.padding(horizontal = 12.dp, vertical = 5.dp)), // Remove padding
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = 16.sp
            ),
            maxLines = 5
        )
        IconButton(
            onClick = onSendButtonClicked,
            modifier = Modifier
//                .padding(end = 10.dp)
                .wrapContentWidth()
                .wrapContentHeight(),

        ) {
            Icon(painter = painterResource(id = R.drawable.send),"send")
        }

    }
}

@Composable
fun MessageList(
    chatLogList:List<Message> = emptyList(),
    modifier :Modifier = Modifier
) {
    var prevDate :Date? =Date(0)
    LazyColumn(
        reverseLayout=true,
        modifier = modifier
            .fillMaxWidth()

    )
    {
        items(chatLogList) {
            var currDate = it.sentTime
            if(!prevDate!!.equalDay(currDate)){
                prevDate = currDate
                DateDivider(currDate)
            }
            MessageItem(it)
        }
    }
}

@Composable
fun MessageItem(messageLog:Message, modifier:Modifier = Modifier){
    val chatBackground =
        when(messageLog.messageFromManager){
            true -> Color(0xFFACC7FA)
            false ->  Color(0xFFDBE2F6)
        }
    Row(
        modifier = modifier
            .fillMaxWidth(),

        horizontalArrangement = when(messageLog.messageFromManager){
            true -> Arrangement.Start
            false ->  Arrangement.End
        }){
        Box(
            modifier = Modifier
                .widthIn(20.dp, 320.dp)
                .wrapContentWidth()
                .padding(10.dp)
                .background(color = chatBackground, shape = RoundedCornerShape(size = 15.dp))
                //            .border(width = 2.dp, color = Color.Black, shape = RoundedCornerShape(15.dp, 15.dp, 15.dp, 15.dp))
                .clip(shape = RoundedCornerShape(15.dp, 15.dp, 15.dp, 15.dp))
        ){
            when(messageLog.messageFromManager){
                true -> MessageContentManager(messageLog)
                false -> MessageContentUser(messageLog) 
            }
        }
    }

}



@Composable
fun DateDivider(
    date:Date? = null,
    modifier:Modifier = Modifier
){
    Row (
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically

    ){
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .weight(1f),
            color = Color.Gray // Optional: You can set the color of the divider
        )
        Text(
            text = date!!.toDayString(),
            modifier = Modifier
                .padding(horizontal = 10.dp),
            fontSize = 12.sp,
            color = Color.Gray
        )
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .weight(1f),
            color = Color.Gray // Optional: You can set the color of the divider
        )
    }
}

//@Preview(showBackground = false, name = "Message Page Preview")
//@Composable
//fun MessagePagePreview() {
//    MessagePage()
//}

@Preview(showBackground = false, name = "chatbar preview")
@Composable
fun MessageInputPreview(){
    MessageInputField()
}


@Preview(showBackground = false, name = "chat item preview")
@Composable
fun MessageItemPreview(){
    Column(){
        MessageItem(Message(0,Date(),false,"ooooooooxoxoxooxoxooxooxoxoxooxoxoxooxoxxoxoxoxoxoxoxoxoxo"))
        MessageItem(Message(1,Date(),true,"how can i help you"))
    }
}

@Preview
@Composable
fun DividerPreview() {
    DateDivider(Date())
}