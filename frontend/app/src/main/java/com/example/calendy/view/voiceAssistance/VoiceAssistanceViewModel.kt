package com.example.calendy.view.voiceAssistance

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.calendy.data.maindb.category.ICategoryRepository
import com.example.calendy.data.maindb.history.IHistoryRepository
import com.example.calendy.data.maindb.message.IMessageRepository
import com.example.calendy.data.maindb.plan.IPlanRepository
import com.example.calendy.data.network.CalendyServerApi
import com.example.calendy.data.rawsqldb.RawSqlDatabase
import com.example.calendy.view.messageview.ManagerAI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class VoiceAssistanceViewModel(
    private val planRepository: IPlanRepository,
    private val messageRepository: IMessageRepository,
    private val categoryRepository: ICategoryRepository,
    private val calendyServerApi: CalendyServerApi,
    private val rawSqlDatabase: RawSqlDatabase,
    private val historyRepository: IHistoryRepository,
) : ViewModel(){

    private val _uiState= MutableStateFlow(VoiceAssistanceUiState())
    val uiState = _uiState.asStateFlow()

    // Created when getSpeechRecognizer is called
    private var speechRecognizer: SpeechRecognizer? = null

    private val managerAi = ManagerAI(planRepository, messageRepository, categoryRepository, calendyServerApi, rawSqlDatabase, historyRepository)

    private fun resetState() {
        _uiState.update { VoiceAssistanceUiState() }
    }

    fun setUserInputText(text: String) {
        _uiState.update { uiState.value.copy(userInputText = text) }
    }

    fun sendRequest(request: String) {
        Log.d("VoiceAssistanceViewModel", "sendRequest: $request")
        managerAi.request(request)
    }
    fun startVoiceRecognition(context: Context) {
        if(_uiState.value.listenerState == VoiceAssistanceState.LISTENING) return
        // Permission is already granted

        resetState()

        // Toggle Speech Recognition

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)

        getSpeechRecognizer(context).startListening(intent)

        _uiState.update { VoiceAssistanceUiState(
            userInputText = "",
            AiText = "캘린디가 듣고 있어요!",
            listenerState = VoiceAssistanceState.LISTENING
        ) }
    }

    fun stopVoiceRecognition(context: Context) {
        getSpeechRecognizer(context).stopListening()
        _uiState.update { current -> current.copy(listenerState = VoiceAssistanceState.DONE) }
    }

    // Event Listener for speech recognizer
    private val recognitionListener: RecognitionListener = object : RecognitionListener {
        private fun activateSpeechRecognition() {

        }

        private fun deactivateSpeechRecognition() {
            speechRecognizer?.cancel()
            speechRecognizer?.destroy()
            speechRecognizer = null
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
            Log.d("VoiceAssistanceViewModel", "onBufferReceived: $buffer")
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
            Log.d("VoiceAssistanceViewModel", "onError: $message")
            _uiState.update { current -> current.copy(AiText = "죄송해요. 문제가 생긴 것 같아요😢", listenerState = VoiceAssistanceState.ERROR) }
            deactivateSpeechRecognition()
        }

        // 인식 결과가 준비되면 호출
        override fun onResults(results: Bundle) {
            val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            // Note: matches[1]은 더 확률이 낮은 인식 결과이다
            val text=matches?.firstOrNull() ?: ""

            _uiState.update { current -> current.copy(userInputText = text, AiText = "알겠습니다. 제게 맡겨주세요!😊", listenerState = VoiceAssistanceState.DONE) }
            sendRequest(text)
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

}

enum class VoiceAssistanceState {
    LISTENING,
    DONE,
    ERROR,
}