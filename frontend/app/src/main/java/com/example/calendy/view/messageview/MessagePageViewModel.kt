package com.example.calendy.view.messagepage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.calendy.data.maindb.category.ICategoryRepository
import com.example.calendy.data.maindb.history.IHistoryRepository
import com.example.calendy.data.maindb.history.ManagerHistory
import com.example.calendy.data.maindb.message.IMessageRepository
import com.example.calendy.data.maindb.message.Message
import com.example.calendy.data.maindb.plan.IPlanRepository
import com.example.calendy.data.maindb.plan.Plan
import com.example.calendy.data.maindb.plan.Schedule
import com.example.calendy.data.maindb.plan.Todo
import com.example.calendy.data.network.CalendyServerApi
import com.example.calendy.data.network.MessageBody
import com.example.calendy.data.rawsqldb.RawSqlDatabase
import com.example.calendy.view.messageview.MessageUIState
import com.example.calendy.view.messageview.QueryType
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

        }

        // 말하기 시작했을 때 호출
        override fun onBeginningOfSpeech() {
            activateSpeechRecognition()
        }

        // 입력받는 소리의 크기를 알려줌
        override fun onRmsChanged(rmsdB: Float) {}

        // 말을 시작하고 인식이 된 audio stream을 buffer에 담는다
        override fun onBufferReceived(buffer: ByteArray) {
            // TODO: Need Test
            _uiState.update{
                currentState ->
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


    fun onMicButtonClicked(context: Context) {
        // Permission is already granted

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

            val gptFirstMessage = Message(
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
//                        gptOriginalMessage.copy(content = "AI 매니저가 살펴보고 있어요")
                        //TODO: Refactor string as constant variable or message type
                        gptFirstMessage.copy(content = "AI_THINKING")
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
                    var queries = resultFromServer.trim('"').split(";").dropLast(1)

                    sqlExecute(queries[0].trim(), gptFirstMessage)
                    for (query in queries.drop(1)) {
                        val gptMessage = Message(
                            id = 0,
                            sentTime = Date(),
                            messageFromManager = true,
                            content = "AI가 생성 중입니다. 잠시만 기다려주세요."
                        ).let {
                            val messageId: Int = messageRepository.insert(it).toInt()
                            it.copy(id = messageId)
                        }
                        //gpt가 처리한 후에 이후 message들을 띄우는게 낫지만, 일단은 sqlExecute이 이미 존재하는 message를 처리하므로 이렇게 한다.
                        sqlExecute(query.trim(), gptMessage)
                    }
                } catch (e: Throwable) {
                    //message 여러개 생성 후에 error날 경우 첫번째만 바꾸게될 것 같은데, 추후 수정 필
                    Log.e("GUN", e.stackTraceToString())
                    messageRepository.update(
                        gptFirstMessage.copy(content = "Send Query 에서 에러가 나타났다")
                    )
                }
            }
        }
    }

    // should update information to gptMessage
    private suspend fun sqlExecute(gptQuery: String, gptMessage: Message) {
        // TODO: Refactor Me
        Log.d("GUN", "Query Start: $gptQuery")
        val startsWith = gptQuery.takeWhile{ it != ' '}.uppercase()
        val queryType = when(startsWith){
            "INSERT" -> QueryType.INSERT
            "UPDATE" -> QueryType.UPDATE
            "DELETE" -> QueryType.DELETE
            "SELECT" -> QueryType.SELECT
            "NO_SUCH_PLAN" -> QueryType.NOT_FOUND
            else -> QueryType.UNEXPECTED
        }


        // if isSchedule is false, should query tod0 db
        val isSchedule = gptQuery.split(" ").run {
            // there is "SCHEDULE" at second or third word
            // INSERT INTO table, UPDATE table, DELETE table, DELETE FROM table
            listOf(this.getOrNull(1), this.getOrNull(2)).any {
                it?.startsWith("SCHEDULE", ignoreCase = true) ?: false
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
        suspend fun insertHistory(gptMessage: Message, isSchedule: Boolean, queryType: QueryType, currentId: Int? = null, savedId: Int? = null){
            if(isSchedule){
                historyRepository.insertHistory(
                    ManagerHistory(
                        messageId = gptMessage.id,
                        isSchedule = isSchedule,
                        revisionType = queryType,
                        currentScheduleId = currentId,
                        savedScheduleId= savedId
                    )
                )

            }else{
                historyRepository.insertHistory(
                    ManagerHistory(
                        messageId = gptMessage.id,
                        isSchedule = isSchedule,
                        revisionType = queryType,
                        currentTodoId = currentId,
                        savedTodoId = savedId
                    )
                )
            }


        }
        suspend fun updateGptResponseMessage(gptMessage: Message, planListSize: Int, queryType: QueryType, isSchedule: Boolean) {
            var messageString: String
            var hasRevision= true
            if(QueryType.NOT_FOUND==queryType){
                hasRevision=false
                messageString= "찾으시는 플랜이 없어요"
            }
            else if(planListSize==0){
                val planType = if (isSchedule) "일정" else "할 일"
                messageString = when (queryType) {
                    QueryType.INSERT    -> "말씀하신 ${planType}을 추가하지 못했어요."
                    QueryType.UPDATE    -> "말씀하신 ${planType}을 찾지 못했어요."
                    QueryType.DELETE    -> "말씀하신 ${planType}을 찾지 못했어요."
                    else                -> "죄송해요. 잘 이해하지 못했어요. "
                }
                hasRevision=false

            }
            else{
                messageString = "AI 매니저가 "
                if (isSchedule) messageString += "일정 "
                else messageString += "할 일 "
                when (queryType) {
                    QueryType.INSERT    -> messageString += "${planListSize}개를 추가했어요"
                    QueryType.UPDATE    -> messageString += "${planListSize}개를 수정했어요"
                    QueryType.DELETE    -> messageString += "${planListSize}개를 삭제했어요"
//                    QueryType.NOT_FOUND -> {messageString = "찾으시는 플랜이 없어요"
//                        hasRevision=false
//                    }
                    else                -> {messageString = "죄송해요. 잘 이해하지 못했어요."
                        hasRevision=false
                    }
                }
                if(planListSize<=0) hasRevision=false

            }
            messageRepository.update(
                gptMessage.copy(
                    content = messageString, hasRevision = hasRevision
                )
            )
        }
        // 우선 기존 data를 모두 삭제해둔다.
        rawSqlDatabase.deleteAll()

        try {
            val planListSize: Int
            when(queryType){
                QueryType.INSERT -> {
                    // RawSqlDB에 INSERT sqlQuery 실행
                    rawSqlDatabase.execSql(gptQuery)
                    // RawSqlDB에서 Select All
                    val planList = rawSqlDatabase.getAllPlans()

                    for (plan in planList) {
                        // 그 결과를 MainDB에 삽입
                        val newPlanId = planRepository.insert(plan).toInt()

                        // Manager가 변경한 사항을 기록

                        insertHistory(gptMessage, isSchedule, queryType, currentId=newPlanId)
                    }

                    //initialize planListSize for updating message

                    planListSize= planList.size
                }
                QueryType.UPDATE -> {
                    val originalPlanList = getAffectedPlansFromGptQuery()

                    // RawSqlDB에 복사
                    for (plan in originalPlanList) {
                        val originalPlanId = plan.id

                        // Saved Plan에 변경되기 전의 plan 저장하기
                        val savedPlanId = historyRepository.insertSavedPlanFromPlan(plan).toInt()

                        // 결과를 Empty DB에 삽입. 이때 plan의 id가 유지된다.
                        rawSqlDatabase.insertFromPlan(plan)

                        // Manager가 변경할 예정인 사항을 기록
                        insertHistory(gptMessage, isSchedule, QueryType.UPDATE, currentId = originalPlanId, savedId = savedPlanId)
                    }

                    // RawSqlDB에 update sqlQuery 실행
                    rawSqlDatabase.execSql(gptQuery)
                    // RawSqlDB에 Select All
                    val planList = rawSqlDatabase.getAllPlans()
                    // 그 결과를 MainDB에 반영하기
                    for (plan in planList) {
                        planRepository.update(plan)
                    }

                    //initialize planListSize for updating message
                    planListSize= planList.size

                }
                QueryType.DELETE ->{
                    val originalPlanList = getAffectedPlansFromGptQuery()

                    // MainDB에서 삭제
                    for (plan in originalPlanList) {
                        // Saved Plan에 변경되기 전의 plan 저장하기
                        val savedPlanId = historyRepository.insertSavedPlanFromPlan(plan).toInt()

                        // 결과를 Calendy DB에서 삭제
                        planRepository.delete(plan)

                        // Manager가 변경한 사항을 기록
                        insertHistory(gptMessage, isSchedule, QueryType.DELETE, savedId = savedPlanId)
                    }

                    //initialize planListSize for updating message
                    planListSize= originalPlanList.size
                }
                QueryType.SELECT->{
                    // RawSqlDB에 INSERT sqlQuery 실행
                    rawSqlDatabase.execSql(gptQuery)
                    // RawSqlDB에서 Select All
                    val planList = rawSqlDatabase.getAllPlans()

                    //initialize planListSize for updating message
                    planListSize= planList.size
                }
                QueryType.NOT_FOUND->{
                    planListSize=0;

                }
                else -> {
                    // if invalid request, say sorry
                    planListSize=-1;

                }


            }
            // Message DB에 Message 넣어주기
            updateGptResponseMessage(gptMessage, planListSize, queryType, isSchedule)

        } catch (e: Throwable) {
            // Catching all throwable may not be good
            Log.e("GUN", e.stackTraceToString())
            messageRepository.update(
                gptMessage.copy(content = "으악! 에러다!", hasRevision = false)
            )
        }

    }

}