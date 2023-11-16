package com.example.calendy.view.messageview

import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.calendy.data.maindb.category.ICategoryRepository
import com.example.calendy.data.maindb.history.IHistoryRepository
import com.example.calendy.data.maindb.history.ManagerHistory
import com.example.calendy.data.maindb.history.RevisionType
import com.example.calendy.data.maindb.message.IMessageRepository
import com.example.calendy.data.maindb.message.Message
import com.example.calendy.data.maindb.plan.IPlanRepository
import com.example.calendy.data.maindb.plan.Plan
import com.example.calendy.data.maindb.plan.Schedule
import com.example.calendy.data.maindb.plan.Todo
import com.example.calendy.data.network.CalendyServerApi
import com.example.calendy.data.network.MessageBody
import com.example.calendy.data.rawsqldb.RawSqlDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessagePageViewModel(
    val planRepository: IPlanRepository,
    val messageRepository: IMessageRepository,
    val categoryRepository: ICategoryRepository,
    val calendyServerApi: CalendyServerApi,
    val rawSqlDatabase: RawSqlDatabase,
    val historyRepository: IHistoryRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MessageUIState())
    val uiState: StateFlow<MessageUIState> = _uiState.asStateFlow()
    private var messageStreamJob: Job? = null

    init {
        getMessages(Date(2023, 0, 1, 0, 0, 0), Date(2024, 0, 1, 0, 0, 0))
    }

    private fun getMessages(startTime: Date, endTime: Date) {
        val flow = messageRepository.getAllMessagesStream()
        messageStreamJob?.cancel()
        messageStreamJob = viewModelScope.launch {
            flow.collect {
                updateMessageList(it)
            }
        }
        Log.d("calendy", "getmessages")
    }

    private fun updateMessageList(messageList: List<Message>) {
        _uiState.update { current -> current.copy(messageLogs = messageList) }
    }

    fun setUserInputText(text: String) {
        _uiState.update { currentState -> currentState.copy(userInputText = text) }
    }

    fun onSendButtonClicked() {
        val userInput = uiState.value.userInputText
        addUserMessage(userInput)
        sendQuery(userInput)
        setUserInputText("")
    }

    private fun addUserMessage(userContent: String) {
        // add user input in text input field to db
        if (userContent.isEmpty()) return
        val newMessage = Message(
            sentTime = Date(), messageFromManager = false, content = userContent
        )
        viewModelScope.launch { messageRepository.insert(newMessage) }
    }

    // sendQuery is called when on send button clicked
    // send message to server and handle result
    private fun sendQuery(requestMessage: String) {
        viewModelScope.launch {
            Log.d("GUN", "send to server $requestMessage")

            val gptOriginalMessage = Message(
                id = 0,
                sentTime = Date(),
                messageFromManager = true,
                content = "AI가 생성 중입니다. 잠시만 기다려주세요."
            ).let {
                val messageId: Int = messageRepository.insert(it).toInt()
                it.copy(id = messageId)
            }

            // TODO: Stream을 쓰지 말기
            //region 모든 DB 정보 string 으로 변환하기
            val allCategories = categoryRepository.getCategoriesStream().first()
            // ex) (1, 과제), (2, 운동), ..., (5, 약속)
            val allCategoriesPrompt = allCategories.joinToString(", ") {
                "(${it.id},${it.title})"
            }

            val t = planRepository.getAllPlansStream().first()
            val allSchedules = t.filterIsInstance<Schedule>()
            val allSchedulesPrompt = allSchedules.joinToString(", ") {
                "(${it.id},${it.title})"
            }

            val allTodos = t.filterIsInstance<Todo>()
            val allTodosPrompt = allTodos.joinToString(", ") {
                "(${it.id},${it.title})"
            }
            //endregion

            withContext(Dispatchers.IO) {
                try {
                    messageRepository.update(
                        gptOriginalMessage.copy(content = "AI 매니저가 살펴보고 있어요")
                    )

                    val resultFromServer = calendyServerApi.sendMessageToServer(
                        MessageBody(
                            message = requestMessage,
                            time = SimpleDateFormat(
                                "yyyy-MM-dd HH:mm:ss", Locale.getDefault()
                            ).format(
                                Date()
                            ),
                            category = allCategoriesPrompt,
                            schedule = allSchedulesPrompt,
                            todo = allTodosPrompt
                        )
                    )

                    // handle result
                    // TODO: Flag - NO_SUCH_TODO 등
                    // TODO: memo에 ; 가 들어가면, GPT가 SQL Injection이나 버그를 유발하는 SQL Query를 반환한다.
                    val queries = resultFromServer.split(";")
                    for (query in queries) {
                        // 가끔 GPT가 "INSERT ... ;" 처럼 쌍따옴표를 붙이기 때문에
                        if (query.trim('"').isNotBlank()) {
                            sqlExecute(query.trim().trim('"'), gptOriginalMessage)
                        }
                    }
                } catch (e: Throwable) {
                    Log.e("GUN", e.stackTraceToString())
                    messageRepository.update(
                        gptOriginalMessage.copy(content = "Send Query 에서 에러가 나타났다")
                    )
                }
            }
        }
    }

    // should update information to gptOriginalMessage
    private suspend fun sqlExecute(gptQuery: String, gptOriginalMessage: Message) {
        // TODO: Refactor Me
        Log.d("GUN", "Query Start: $gptQuery")
        val isInsert = gptQuery.startsWith("INSERT", ignoreCase = true)
        val isUpdate = gptQuery.startsWith("UPDATE", ignoreCase = true)
        val isDelete = gptQuery.startsWith("DELETE", ignoreCase = true)

        // if isSchedule is false, should query tod0 db
        val isSchedule = gptQuery.split(" ").run {
            // there is "SCHEDULE" at second or third word
            // INSERT INTO table, UPDATE table, DELETE table, DELETE FROM table
            listOf(this.getOrNull(1), this.getOrNull(2)).any {
                it.equals("SCHEDULE", ignoreCase = true)
            }
        }
        val queryTable = if (isSchedule) "schedule" else "todo"


        fun getAffectedPlansFromGptQuery(): List<Plan> {
            // SELECT table where ... 로 교체
            // ex) UPDATE table SET ... WHERE ...
            val whereStartsAt = gptQuery.indexOf(" WHERE ", ignoreCase = true)
            val whereString = if (whereStartsAt!=-1) {
                gptQuery.substring(startIndex = whereStartsAt).trim()
            } else "" // if WHERE is not present, use empty string

            // Calendy DB의 DAO.rawQuery 로 UPDATE에 영향을 받는 planList 받기
            val calendySelectQuery = SimpleSQLiteQuery(
                "SELECT * FROM $queryTable $whereString",
            )

            return when (isSchedule) {
                true  -> planRepository.getSchedulesViaQuery(calendySelectQuery)
                false -> planRepository.getTodosViaQuery(calendySelectQuery)
            }
        }

        // 우선 기존 data를 모두 삭제해둔다.
        rawSqlDatabase.deleteAll()

        try {
            if (isInsert) {
                // RawSqlDB에 INSERT sqlQuery 실행
                rawSqlDatabase.execSql(gptQuery)
                // RawSqlDB에서 Select All
                val planList = rawSqlDatabase.getAllPlans()

                for (plan in planList) {
                    // 그 결과를 MainDB에 삽입
                    val newPlanId = planRepository.insert(plan).toInt()

                    // Manager가 변경한 사항을 기록
                    when (plan) {
                        is Schedule -> historyRepository.insertHistory(
                            ManagerHistory(
                                messageId = gptOriginalMessage.id,
                                isSchedule = true,
                                revisionType = RevisionType.INSERT,
                                currentScheduleId = newPlanId,
                            )
                        )

                        is Todo     -> historyRepository.insertHistory(
                            ManagerHistory(
                                messageId = gptOriginalMessage.id,
                                isSchedule = false,
                                revisionType = RevisionType.INSERT,
                                currentTodoId = newPlanId,
                            )
                        )
                    }
                }

                // Message DB에 Message 넣어주기
                messageRepository.update(
                    gptOriginalMessage.copy(
                        content = "AI 매니저가 일정 ${planList.size}개를 추가했어요", hasRevision = true
                    )
                )
            } else if (isUpdate) {
                val originalPlanList = getAffectedPlansFromGptQuery()

                // RawSqlDB에 복사
                for (plan in originalPlanList) {
                    val originalPlanId = plan.id

                    // Saved Plan에 변경되기 전의 plan 저장하기
                    val savedPlanId = historyRepository.insertSavedPlanFromPlan(plan).toInt()

                    // 결과를 Empty DB에 삽입. 이때 plan의 id가 유지된다.
                    rawSqlDatabase.insertFromPlan(plan)

                    // Manager가 변경할 예정인 사항을 기록
                    when (plan) {
                        is Schedule -> historyRepository.insertHistory(
                            ManagerHistory(
                                messageId = gptOriginalMessage.id,
                                isSchedule = true,
                                revisionType = RevisionType.UPDATE,
                                currentScheduleId = originalPlanId,
                                savedScheduleId = savedPlanId,
                            )
                        )

                        is Todo     -> historyRepository.insertHistory(
                            ManagerHistory(
                                messageId = gptOriginalMessage.id,
                                isSchedule = false,
                                revisionType = RevisionType.UPDATE,
                                currentTodoId = originalPlanId,
                                savedTodoId = savedPlanId,
                            )
                        )
                    }
                }

                // RawSqlDB에 update sqlQuery 실행
                rawSqlDatabase.execSql(gptQuery)
                // RawSqlDB에 Select All
                val planList = rawSqlDatabase.getAllPlans()
                // 그 결과를 MainDB에 반영하기
                for (plan in planList) {
                    planRepository.update(plan)
                }

                // Message DB에 Message 넣어주기
                messageRepository.update(
                    gptOriginalMessage.copy(
                        content = "AI 매니저가 일정 ${planList.size}개를 수정했어요", hasRevision = true
                    )
                )
            } else if (isDelete) {
                val originalPlanList = getAffectedPlansFromGptQuery()

                // MainDB에서 삭제
                for (plan in originalPlanList) {
                    // Saved Plan에 변경되기 전의 plan 저장하기
                    val savedPlanId = historyRepository.insertSavedPlanFromPlan(plan).toInt()

                    // 결과를 Calendy DB에서 삭제
                    planRepository.delete(plan)

                    // Manager가 변경한 사항을 기록
                    when (plan) {
                        is Schedule -> historyRepository.insertHistory(
                            ManagerHistory(
                                messageId = gptOriginalMessage.id,
                                isSchedule = true,
                                revisionType = RevisionType.DELETE,
                                currentScheduleId = null,
                                savedScheduleId = savedPlanId,
                            )
                        )

                        is Todo     -> historyRepository.insertHistory(
                            ManagerHistory(
                                messageId = gptOriginalMessage.id,
                                isSchedule = false,
                                revisionType = RevisionType.DELETE,
                                currentTodoId = null,
                                savedTodoId = savedPlanId,
                            )
                        )
                    }
                }

                // Message DB에 Message 넣어주기
                messageRepository.update(
                    gptOriginalMessage.copy(
                        content = "AI 매니저가 일정 ${originalPlanList.size}개를 삭제했어요", hasRevision = true
                    )
                )
            } else {
                // if invalid request, say sorry
                messageRepository.update(
                    gptOriginalMessage.copy(content = "죄송해요. 잘 이해하지 못했어요.", hasRevision = false)
                )

            }
        } catch (e: Throwable) {
            // Catching all throwable may not be good
            Log.e("GUN", e.stackTraceToString())
            messageRepository.update(
                gptOriginalMessage.copy(content = "으악! 에러다!", hasRevision = false)
            )
        }
    }

    // TODO: UiState에 통합하기
    val speechRecognizerState = MutableStateFlow("")

    // Code from Tistory. Event Listener for speech recognizer
    val recognitionListener: RecognitionListener = object : RecognitionListener {
        // 말하기 시작할 준비가되면 호출
        override fun onReadyForSpeech(params: Bundle) {

        }

        // 말하기 시작했을 때 호출
        override fun onBeginningOfSpeech() {
            speechRecognizerState.value = "Start Speech"
        }

        // 입력받는 소리의 크기를 알려줌
        override fun onRmsChanged(rmsdB: Float) {}

        // 말을 시작하고 인식이 된 audio stream을 buffer에 담는다
        override fun onBufferReceived(buffer: ByteArray) {}

        // 말하기를 중지하면 호출
        override fun onEndOfSpeech() {
            speechRecognizerState.value = "End Speech"
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
            speechRecognizerState.value = "ERROR: $message"
        }

        // 인식 결과가 준비되면 호출
        override fun onResults(results: Bundle) {
            val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            // matches[1]은 더 확률이 낮은 인식 결과이다
            speechRecognizerState.value = matches?.firstOrNull() ?: ""

            // 인식 결과를 Send Query하기
            _uiState.update { currentState ->
                currentState.copy(userInputText = matches?.firstOrNull() ?: "")
            }
            onSendButtonClicked()
        }

        // 부분 인식 결과를 사용할 수 있을 때 호출
        override fun onPartialResults(partialResults: Bundle) {
            val matches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            Log.d("GUN", "OnPartialResults")
            for (i in matches!!.indices) Log.d("GUN", matches[i])
        }

        // 향후 이벤트를 추가하기 위해 예약
        override fun onEvent(eventType: Int, params: Bundle) {}
    }
}