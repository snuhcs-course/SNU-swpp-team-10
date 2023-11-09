package com.example.calendy.view.messagepage

import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.calendy.data.MessageBody
import com.example.calendy.data.category.ICategoryRepository
import com.example.calendy.data.emptydb.EmptyDatabase
import com.example.calendy.data.log.ILogPlanRepository
import com.example.calendy.data.log.LogPlan
import com.example.calendy.data.log.LogSchedule
import com.example.calendy.data.log.LogTodo
import com.example.calendy.data.message.IMessageRepository
import com.example.calendy.data.message.Message
import com.example.calendy.data.network.RetrofitClient
import com.example.calendy.data.plan.IPlanRepository
import com.example.calendy.data.plan.Schedule
import com.example.calendy.data.plan.Todo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessagePageViewModel(
    //TODO: chat repository needed
    val planRepository: IPlanRepository,
    val messageRepository: IMessageRepository,
    val categoryRepository: ICategoryRepository,
    val emptyDatabase: EmptyDatabase,
    val logPlanRepository: ILogPlanRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MessageUIState())
    val uiState: StateFlow<MessageUIState> = _uiState.asStateFlow()
    var job: Job? = null

    init {
        getMessages(Date(2023, 0, 1, 0, 0, 0), Date(2024, 0, 1, 0, 0, 0))
    }

    fun setUserInputText(text: String) {
        _uiState.update { currentState -> currentState.copy(userInputText = text) }
    }

    fun getMessages(startTime: Date, endTime: Date) {
        val flow = messageRepository.getAllMessages()
        job?.cancel()
        job = viewModelScope.launch {
            flow.collect {
                updateMessageList(it)
            }
        }
        Log.d("calendy", "getmessages")
    }



    fun addUserMessage(text: String) {
        // add user input in text input field to db 
        val userContent = text
        if (userContent.isNullOrEmpty()) return
        val newMessage = Message(
            id = 0,
            sentTime = Date(),
            messageFromManager = false,
            content = userContent
        )
        insertMessage(newMessage)
    }

    fun insertMessage(message: Message) {
        viewModelScope.launch { messageRepository.insert(message) }
    }

    private fun updateMessageList(messageList: List<Message>) {
        _uiState.update { current -> current.copy(messageLogs = messageList) }
    }

    fun onSendButtonClicked() {
        val userInput = uiState.value.userInputText
        addUserMessage(userInput)
        sendQuery(userInput)
        setUserInputText("")
    }

    fun sendQuery(requestMessage: String) {
        viewModelScope.launch {
            Log.d("GUN", "send to server $requestMessage")

            val tempMessage = Message(
                id = 0,
                sentTime = Date(),
                messageFromManager = true,
                content = "AI가 생성 중입니다. 잠시만 기다려주세요."
            )
            val messageId: Int = messageRepository.insert(tempMessage).toInt()
            val gptOriginalMessage = tempMessage.copy(id = messageId)

            val allCategories = categoryRepository.getCategoriesStream().first()
            // ex) (1, 컴퓨터 구조), ...
            val allCategoriesPrompt = allCategories.joinToString(", ") {
                "(${it.id},${it.title})"
            }

            val t = planRepository.getAllPlans().first()
            val schedulePromptBuilder = StringBuilder()
            val todoPromptBuilder = StringBuilder()
            // ex) (1, 컴퓨터 구조), ...
            for (plan in t) {
                when (plan) {
                    is Schedule -> schedulePromptBuilder.append("(${plan.id},${plan.title}), ")
                    is Todo -> todoPromptBuilder.append("(${plan.id},${plan.title}), ")
                }
            }

            val allSchedules = schedulePromptBuilder.toString().dropLast(2)
            val allTodos = todoPromptBuilder.toString().dropLast(2)
            Log.d("GUN", allSchedules)
            Log.d("GUN", allTodos)

            withContext(Dispatchers.IO) {
                try {
                    messageRepository.update(
                        gptOriginalMessage.copy(content = "AI 매니저가 살펴보고 있어요")
                    )

                    val result = RetrofitClient.instance.sendMessageToServer(
                        MessageBody(
                            message = requestMessage,
                            time = SimpleDateFormat(
                                "yyyy-MM-dd HH:mm:ss", Locale.getDefault()
                            ).format(
                                Date()
                            ),
                            category = allCategoriesPrompt,
                            schedule = allSchedules,
                            todo = allTodos
                        )
                    )

                    // TODO: Flag - NO_SUCH_TODO 등
                    // TODO: memo에 ; 가 들어가면, GPT가 SQL Injection이나 버그를 유발하는 SQL Query를 반환한다.
                    val queries = result.split(";")
                    for (query in queries) {
                        if (query.trim('"').isNotBlank()) {
                            sqlExecute(query.trim().trim('"'), gptOriginalMessage)
                        }
                    }
                } catch (e: Throwable) {
                    Log.e("GUN", e.stackTraceToString())
                }
            }
        }
    }


    // TODO: Message ID 받아서, Log DB에 넣을 때 message id 넣어놔야 한다.
    // Log DB에 INSERT, UPDATE, DELETE Flag 기록해두기 (Schedule, Tod0)
    // log_schedule, log_tod0
    private suspend fun sqlExecute(gptQuery: String, gptOriginalMessage: Message) {
        // TODO: Refactor Me
        Log.d("GUN", "Query Start: $gptQuery")
        val isInsert = gptQuery.startsWith("INSERT", ignoreCase = true)
        val isUpdate = gptQuery.startsWith("UPDATE", ignoreCase = true)
        val isDelete = gptQuery.startsWith("DELETE", ignoreCase = true)

        // if isSchedule is false, should query tod0 db
        val isSchedule = gptQuery.split(" ").run {
            // there is "SCHEDULE" at second or third word
            // INSERT INTO table
            // UPDATE table
            // DELETE table
            // DELETE FROM table
            listOf(this.getOrNull(1), this.getOrNull(2)).any {
                it.equals("SCHEDULE", ignoreCase = true)
            }
        }
        val queryTable = if (isSchedule) "schedule" else "todo"

        // 모두 삭제해둔다.
        emptyDatabase.deleteAll()

        try {
            if (isInsert) {
                // EmptyDB에 sqlQuery 실행 - emptyDB.query()
                emptyDatabase.openHelper.writableDatabase.execSQL(gptQuery)
                // emptyDB에 Select All
                val planList = emptyDatabase.getAllPlansStream().first()
                // 그 결과를 calendyDB에 삽입
                for (plan in planList) {
                    planRepository.insertPlan(plan)

                    Log.d("GUN", "Before Log DB")
                    // Log DB에 변경된 plan 기록하기
                    when (plan) {
                        is Schedule -> logPlanRepository.insertLogPlan(
                            LogSchedule(
                                messageId = gptOriginalMessage.id,
                                logType = "INSERT",
                                planId = null // TODO: plan id 설정하기!
                            )
                        )

                        is Todo -> logPlanRepository.insertLogPlan(
                            LogTodo(
                                messageId = gptOriginalMessage.id,
                                logType = "INSERT",
                                planId = null // TODO: plan id 설정하기!
                            )
                        )
                    }
                }

                // Message DB에 Message 넣어주기
                messageRepository.update(
                    gptOriginalMessage.copy(content = "AI 매니저가 일정을 추가했어요", hasLogPlan = true)
                )
            } else if (isUpdate) {
                // SELECT table where ... 로 교체
                // ex) UPDATE table SET ... WHERE ...
                val whereStartsAt = gptQuery.indexOf(" WHERE ", ignoreCase = true)
                val whereString = if (whereStartsAt != -1) {
                    gptQuery.substring(startIndex = whereStartsAt).trim()
                } else "" // if WHERE is not present, use empty string

                // Calendy DB의 DAO.rawQuery 로 UPDATE에 영향을 받는 planList 받기
                val calendySelectQuery = SimpleSQLiteQuery(
                    "SELECT * FROM $queryTable $whereString",
                )
                // TODO: Check if this works. (SELECT * FROM schedule) in todoRepository
                val originalPlanList = when (isSchedule) {
                    true -> planRepository.getSchedulesViaQuery(calendySelectQuery)
                    false -> planRepository.getTodosViaQuery(calendySelectQuery)
                }

                // 결과를 Empty DB에 삽입
                for (plan in originalPlanList) {
                    when (plan) {
                        is Schedule -> emptyDatabase.scheduleDao().insert(plan)
                        is Todo -> emptyDatabase.todoDao().insert(plan)
                    }

                    // Log DB에 변경되기 전의 plan 기록하기
                    when (plan) {
                        is Schedule -> logPlanRepository.insertLogPlan(
                            LogSchedule(
                                messageId = gptOriginalMessage.id,
                                logType = "UPDATE",
                                planId = plan.id,
                                title = plan.title,
                                startTime = plan.startTime,
                                endTime = plan.endTime,
                                memo = plan.memo,
                                repeatGroupId = plan.repeatGroupId,
                                categoryId = plan.categoryId,
                                priority = plan.priority!!,
                                showInMonthlyView = plan.showInMonthlyView,
                                isOverridden = plan.isOverridden,
                            )
                        )

                        is Todo -> logPlanRepository.insertLogPlan(
                            LogTodo(
                                messageId = gptOriginalMessage.id,
                                logType = "UPDATE",
                                planId = plan.id,
                                title = plan.title,
                                dueTime = plan.dueTime,
                                memo = plan.memo,
                                repeatGroupId = plan.repeatGroupId,
                                categoryId = plan.categoryId,
                                priority = plan.priority!!,
                                showInMonthlyView = plan.showInMonthlyView,
                                isOverridden = plan.isOverridden,
                                yearly = plan.yearly,
                                monthly = plan.monthly,
                                daily = plan.daily,
                                complete = plan.complete,
                            )
                        )
                    }
                }
                // EmptyDB에 update sqlQuery 실행 - emptyDB.query()
                emptyDatabase.openHelper.writableDatabase.execSQL(gptQuery)
                // emptyDB에 Select All
                val planList = emptyDatabase.getAllPlansStream().first()
                // 그 결과를 calendyDB에 삽입
                for (plan in planList) {
                    planRepository.updatePlan(plan)
                }

                // Message DB에 Message 넣어주기
                messageRepository.update(
                    gptOriginalMessage.copy(content = "AI 매니저가 일정을 수정했어요", hasLogPlan = true)
                )
            } else if (isDelete) {
                // SELECT where ... 로 교체
                // ex) DELETE table WHERE ...
                val whereStartsAt = gptQuery.indexOf(" WHERE ", ignoreCase = true)
                val whereString = if (whereStartsAt != -1) {
                    gptQuery.substring(startIndex = whereStartsAt).trim()
                } else "" // if WHERE is not present, use empty string

                // Calendy DB의 DAO.rawQuery 로 planList 받기
                val calendySelectQuery = SimpleSQLiteQuery(
                    "SELECT * FROM $queryTable $whereString",
                )
                val originalPlanList = when (isSchedule) {
                    true -> planRepository.getSchedulesViaQuery(calendySelectQuery)
                    false -> planRepository.getTodosViaQuery(calendySelectQuery)
                }

                // 결과를 Calendy DB에서 삭제
                for (plan in originalPlanList) {
                    planRepository.deletePlan(plan)

                    // Log DB에 변경되기 전의 plan 기록하기
                    when (plan) {
                        is Schedule -> logPlanRepository.insertLogPlan(
                            LogSchedule(
                                messageId = gptOriginalMessage.id,
                                logType = "DELETE",
                                planId = null,
                                title = plan.title,
                                startTime = plan.startTime,
                                endTime = plan.endTime,
                                memo = plan.memo,
                                repeatGroupId = plan.repeatGroupId,
                                categoryId = plan.categoryId,
                                priority = plan.priority!!,
                                showInMonthlyView = plan.showInMonthlyView,
                                isOverridden = plan.isOverridden,
                            )
                        )

                        is Todo -> logPlanRepository.insertLogPlan(
                            LogTodo(
                                messageId = gptOriginalMessage.id,
                                logType = "DELETE",
                                planId = null,
                                title = plan.title,
                                dueTime = plan.dueTime,
                                memo = plan.memo,
                                repeatGroupId = plan.repeatGroupId,
                                categoryId = plan.categoryId,
                                priority = plan.priority!!,
                                showInMonthlyView = plan.showInMonthlyView,
                                isOverridden = plan.isOverridden,
                                yearly = plan.yearly,
                                monthly = plan.monthly,
                                daily = plan.daily,
                                complete = plan.complete,
                            )
                        )
                    }
                }

                // Message DB에 Message 넣어주기
                messageRepository.update(
                    gptOriginalMessage.copy(content = "AI 매니저가 일정을 삭제했어요", hasLogPlan = true)
                )
            }
            else
            {
                // if invalid request, say sorry
                messageRepository.update(
                    gptOriginalMessage.copy(content = "죄송해요. 잘 이해하지 못했어요.", hasLogPlan = false)
                )

            }
        } catch (e: Throwable) {
            // TODO: Catching all throwable may not be good
            Log.e("GUN", e.stackTraceToString())
            messageRepository.update(
                gptOriginalMessage.copy(content = "으악! 에러다!", hasLogPlan = false)
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
                SpeechRecognizer.ERROR_AUDIO -> "오디오 에러"
                SpeechRecognizer.ERROR_CLIENT -> "클라이언트 에러"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "퍼미션 없음"
                SpeechRecognizer.ERROR_NETWORK -> "네트워크 에러"
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "네트웍 타임아웃"
                SpeechRecognizer.ERROR_NO_MATCH -> "찾을 수 없음"
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RECOGNIZER 가 바쁨"
                SpeechRecognizer.ERROR_SERVER -> "서버가 이상함"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "말하는 시간초과"
                else -> "알 수 없는 오류임"
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