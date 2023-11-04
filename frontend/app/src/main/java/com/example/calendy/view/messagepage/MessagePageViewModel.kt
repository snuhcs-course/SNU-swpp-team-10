package com.example.calendy.view.messagepage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calendy.data.message.IMessageRepository
import com.example.calendy.data.message.Message
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

class MessagePageViewModel(
    //TODO: chat repository needed
    val messageRepository : IMessageRepository
) : ViewModel()
{

    private val _uiState = MutableStateFlow(MessageUIState())
    val uiState: StateFlow<MessageUIState> = _uiState.asStateFlow()
    var job : Job? = null

    init {
        getMessages(Date(2023,0,1,0,0,0),Date(2024,0,1,0,0,0))
    }

    fun setUserInputText(text:String){
        _uiState.update { currentState -> currentState.copy(userInputText = text) }
    }

    fun getMessages(startTime:Date,endTime:Date)
    {
        val flow = messageRepository.getAllMessages()
        job?.cancel()
        job = viewModelScope.launch {
            flow.collect {
                updateMessageList(it)
            }
        }
        Log.d("calendy","getmessages")
    }
    fun getAllMessages():StateFlow<List<Message>>
    {
        val messages = messageRepository.getAllMessages().stateIn(
            scope = viewModelScope,
            initialValue = emptyList<Message>(),
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000)
        )
        return  messages
    }

    fun addUserMessage(){
        // add user input in text input field to db 
        val userContent = _uiState.value.userInputText
        if(userContent.isNullOrEmpty()) return
        val newMessage = Message(
            id=0,
            sentTime = Date(),
            messageFromManager=false,
            content = userContent
        )
        insertMessage(newMessage)
    }
    fun insertMessage(message:Message){
        viewModelScope.launch { messageRepository.insert(message) }
    }

    private fun updateMessageList(messageList:List<Message>){
        _uiState.update { current -> current.copy(messageLogs = messageList) }
    }
}