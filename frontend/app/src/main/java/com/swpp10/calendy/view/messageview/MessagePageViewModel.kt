package com.swpp10.calendy.view.messagepage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.swpp10.calendy.data.maindb.message.IMessageRepository
import com.swpp10.calendy.data.maindb.message.Message
import com.swpp10.calendy.data.maindb.plan.IPlanRepository
import com.swpp10.calendy.utils.afterDays
import com.swpp10.calendy.view.messageview.ManagerHelp
import com.swpp10.calendy.view.messageview.MessageUIState
import com.swpp10.calendy.view.messageview.SendMessageWorker
import com.swpp10.calendy.view.messageview.UATDataWorker
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date


class MessagePageViewModel(
    val planRepository: IPlanRepository,
    val messageRepository: IMessageRepository,
    val workManager: WorkManager
) : ViewModel() {
    // TODO: Refactor Idea: Separate UI Part, Voice Recognition Part, Server Communication Part

    private val _uiState = MutableStateFlow(MessageUIState())
    val uiState: StateFlow<MessageUIState> = _uiState.asStateFlow()
    private var messageStreamJob: Job? = null

    init {
        getMessages()
    }

    private fun getMessages() {
        val flow = messageRepository.getAllMessagesStream()
        messageStreamJob?.cancel()
        messageStreamJob = viewModelScope.launch {
            flow.collect {
                updateMessageList(it)
            }
        }
    }

    private fun updateMessageList(messageList: List<Message>) {
        _uiState.update { current -> current.copy(messageLogs = messageList) }
    }

    fun setUserInputText(text: String) {
        _uiState.update { currentState -> currentState.copy(userInputText = text) }
    }

    fun onSendButtonClicked() {
        val userInput = uiState.value.userInputText
        setUserInputText("")
        if (userInput.contentEquals("/uat", ignoreCase = true)) {
            val setDataRequest: OneTimeWorkRequest =
                OneTimeWorkRequestBuilder<UATDataWorker>().build()
            workManager.enqueue(setDataRequest)
            return
        }
        if (userInput.isNotBlank()) {
            sendQuery(userInput)
        }
    }

    //region Speech Recognition
    // Created when getSpeechRecognizer is called
    private var speechRecognizer: SpeechRecognizer? = null

    // Event Listener for speech recognizer
    private val recognitionListener: RecognitionListener = object : RecognitionListener {
        private fun activateSpeechRecognition() {
            _uiState.update { currentState ->
                currentState.copy(isMicButtonClicked = true)
            }
        }

        private fun deactivateSpeechRecognition() {
            _uiState.update { currentState ->
                currentState.copy(isMicButtonClicked = false)
            }
        }

        // 말하기 시작할 준비가되면 호출
        override fun onReadyForSpeech(params: Bundle) {
            activateSpeechRecognition()
        }

        // 말하기 시작했을 때 호출
        override fun onBeginningOfSpeech() {

        }

        // 입력받는 소리의 크기를 알려줌
        override fun onRmsChanged(rmsdB: Float) {}

        // 말을 시작하고 인식이 된 audio stream을 buffer에 담는다
        override fun onBufferReceived(buffer: ByteArray) {
            // TODO: Need Test
            _uiState.update { currentState ->
                var prevText = currentState.userInputText
                prevText += buffer.toString()
                currentState.copy(userInputText = prevText)
            }
        }

        // 말하기를 중지하면 호출
        override fun onEndOfSpeech() {

        }

        // 오류 발생했을 때 호출
        override fun onError(error: Int) {
            val message = when (error) {
                SpeechRecognizer.ERROR_AUDIO                    -> "오디오 에러"
                SpeechRecognizer.ERROR_CLIENT                   -> "클라이언트 에러"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "퍼미션 없음"
                SpeechRecognizer.ERROR_NETWORK                  -> "네트워크 에러"
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT          -> "네트웍 타임아웃"
                SpeechRecognizer.ERROR_NO_MATCH                 -> "찾을 수 없음"
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY          -> "RECOGNIZER 가 바쁨"
                SpeechRecognizer.ERROR_SERVER                   -> "서버가 이상함"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT           -> "말하는 시간초과"
                else                                            -> "알 수 없는 오류임"
            }
            setUserInputText("Speech Recognition ERROR: $message")
            deactivateSpeechRecognition()
        }

        // 인식 결과가 준비되면 호출
        override fun onResults(results: Bundle) {
            val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            // Note: matches[1]은 더 확률이 낮은 인식 결과이다
            setUserInputText(matches?.firstOrNull() ?: "")
            onSendButtonClicked()
            deactivateSpeechRecognition()
        }

        // 부분 인식 결과를 사용할 수 있을 때 호출
        override fun onPartialResults(partialResults: Bundle) {
            val matches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            // Note: matches[1]은 더 확률이 낮은 인식 결과이다
            setUserInputText(matches?.firstOrNull() ?: "")
        }

        // 향후 이벤트를 추가하기 위해 예약
        override fun onEvent(eventType: Int, params: Bundle) {}
    }

    private fun getSpeechRecognizer(context: Context): SpeechRecognizer {
        return speechRecognizer ?: SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(recognitionListener)
        }.also {
            speechRecognizer = it
        }
    }
    //endregion

    fun onMicButtonClicked(context: Context) {
        // Permission is already granted
        setUserInputText("")

        // Toggle Speech Recognition
        if (uiState.value.isMicButtonClicked) {
            getSpeechRecognizer(context).stopListening()
        } else {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)

            getSpeechRecognizer(context).startListening(intent)
        }
    }

    private suspend fun addUserMessage(requestMessage: String): Int {
        val newMessage = Message(
            sentTime = Date(), messageFromManager = false, content = requestMessage
        )
        val userMessageId = messageRepository.insert(newMessage)
        // Add Self reference
        messageRepository.update(
            newMessage.copy(
                id = userMessageId, userMessageId = userMessageId
            )
        )
        return userMessageId
    }

    fun onHelpButtonClicked() {
        viewModelScope.launch {
            val userMessageId = addUserMessage(ManagerHelp.HELP_USER)
            for (message in ManagerHelp.HELP_MESSAGE_LIST) {
                messageRepository.insert(
                    Message(
                        sentTime = Date(),
                        messageFromManager = true,
                        content = message,
                        userMessageId = userMessageId
                    )
                )
            }
        }
    }

    // sendQuery is called when on send button clicked
    // SendMessageWorker will send message to server and handle result
    private fun sendQuery(requestMessage: String) {
        // add user input in text input field to db
        if (requestMessage.isEmpty()) return
        viewModelScope.launch {
            val userMessageId = addUserMessage(requestMessage)

            // Send request to server and ExecuteSQL
            val inputData = Data.Builder()
                .putString("requestMessage", requestMessage)
                .putInt("userMessageId", userMessageId)
                .build()

            val sendWorkRequest: OneTimeWorkRequest =
                OneTimeWorkRequestBuilder<SendMessageWorker>().setInputData(inputData).build()

            workManager.enqueue(sendWorkRequest)
        }
    }
}