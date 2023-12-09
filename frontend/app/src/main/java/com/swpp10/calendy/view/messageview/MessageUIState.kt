package com.swpp10.calendy.view.messageview

import com.swpp10.calendy.data.maindb.message.Message


data class MessageUIState (
    val userInputText:String = "",
    val messageLogs: List<Message> = emptyList(),
    val isMicButtonClicked: Boolean = false,
)