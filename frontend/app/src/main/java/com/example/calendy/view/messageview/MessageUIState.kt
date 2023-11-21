package com.example.calendy.view.messageview

import com.example.calendy.data.maindb.message.Message


data class MessageUIState (
    val userInputText:String = "",
    val messageLogs: List<Message> = emptyList(),
    val isMicButtonClicked: Boolean = false,
)