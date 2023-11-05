package com.example.calendy.view.messagepage

import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.calendy.data.CalendyDatabase
import com.example.calendy.data.MessageBody
import com.example.calendy.data.emptydb.EmptyDatabase
import com.example.calendy.data.network.RetrofitClient
import com.example.calendy.data.plan.Schedule
import com.example.calendy.data.plan.Todo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SqlExecutionViewModel(
    val calendyDatabase: CalendyDatabase, val emptyDatabase: EmptyDatabase
) : ViewModel() {
    fun sendQuery(requestMessage: String) {
        viewModelScope.launch {
            Log.d("GUN", "send to server $requestMessage")

            val allCategories = calendyDatabase.categoryDao().getCategoriesStream().first()
            // ex) (1, 컴퓨터 구조), ...
            val allCategoriesPrompt = allCategories.joinToString(", ") {
                "(${it.id},${it.title})"
            }

            val allSchedules = calendyDatabase.scheduleDao().getAllSchedule().first()
            // ex) (1, 컴퓨터 구조), ...
            val schedulePrompt = allSchedules.joinToString(", ") {
                "(${it.id},${it.title})"
            }

            val allTodos = calendyDatabase.todoDao().getAllTodo().first()
            // ex) (1, 컴퓨터 구조), ...
            val todoPrompt = allTodos.joinToString(", ") {
                "(${it.id},${it.title})"
            }

            withContext(Dispatchers.IO) {
                val result = RetrofitClient.instance.sendMessageToServer(
                    MessageBody(
                        message = requestMessage,
                        time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(
                            Date()
                        ),
                        category = allCategoriesPrompt,
                        schedule = schedulePrompt,
                        todo = todoPrompt
                    )
                )

                // TODO: Flag - NO_SUCH_TODO 등
                val queries = result.split(";")
                for (query in queries) {
                    Log.d("GUN", query.trim())
                    sqlExecute(query.trim())
                }
            }
        }
    }

    fun testLocal() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                sqlExecute("INSERT INTO schedule (title, start_time, end_time, priority) VALUES ('컴퓨터구조 시험', datetime('now', '+1 day', '3 hours'), datetime('now', '+1 day', '4 hours'), 1)")
                sqlExecute("UPDATE schedule SET start_time = datetime('2023-11-06 00:00:00'), end_time = datetime('2023-11-06 23:59:59') WHERE title = '컴퓨터구조 시험'")
                sqlExecute("DELETE schedule WHERE title = '컴퓨터구조 시험'")
                sqlExecute("DELETE schedule")
            }
        }
    }

    // TODO: Message ID 받아서, Log DB에 넣을 때 message id 넣어놔야 한다.
    // Log DB에 INSERT, UPDATE, DELETE Flag 기록해두기 (Schedule, Tod0)
    // log_schedule, log_tod0
    private suspend fun sqlExecute(gptQuery: String) {
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
        val logTable = "log_$queryTable"

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
                    when (plan) {
                        is Schedule -> calendyDatabase.scheduleDao().insert(plan)
                        is Todo -> calendyDatabase.todoDao().insert(plan)
                    }
                    // TODO: Log DB에 변경된 plan 기록하기
                }

                // TODO: Message DB에 Message 넣어주기
            } else if (isUpdate) {
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
                val originalPlanList = if (isSchedule) {
                    calendyDatabase.scheduleDao().getSchedulesViaQuery(calendySelectQuery)
                } else {
                    calendyDatabase.todoDao().getTodosViaQuery(calendySelectQuery)
                }

                // 결과를 Empty DB에 삽입
                for (plan in originalPlanList) {
                    when (plan) {
                        is Schedule -> emptyDatabase.scheduleDao().insert(plan)
                        is Todo -> emptyDatabase.todoDao().insert(plan)
                    }
                    // TODO: Log DB에 UPDATE 이전의 기록 보존하기
                }
                // EmptyDB에 update sqlQuery 실행 - emptyDB.query()
                emptyDatabase.openHelper.writableDatabase.execSQL(gptQuery)
                // emptyDB에 Select All
                val planList = emptyDatabase.getAllPlansStream().first()
                // 그 결과를 calendyDB에 삽입
                for (plan in planList) {
                    when (plan) {
                        is Schedule -> calendyDatabase.scheduleDao().update(plan)
                        is Todo -> calendyDatabase.todoDao().update(plan)
                    }
                }

                // TODO: Message DB에 Message 넣어주기
            } else if (isDelete) {
                // SELECT where ... 로 교체
                // ex) DELETE table WHERE ...
                val whereStartsAt = gptQuery.indexOf(" WHERE ", ignoreCase = true)
                val whereString = if (whereStartsAt!=-1) {
                    gptQuery.substring(startIndex = whereStartsAt).trim()
                } else "" // if WHERE is not present, use empty string

                // Calendy DB의 DAO.rawQuery 로 planList 받기
                val calendySelectQuery = SimpleSQLiteQuery(
                    "SELECT * FROM $queryTable $whereString",
                )
                val originalPlanList = if (isSchedule) {
                    calendyDatabase.scheduleDao().getSchedulesViaQuery(calendySelectQuery)
                } else {
                    calendyDatabase.todoDao().getTodosViaQuery(calendySelectQuery)
                }

                // 결과를 Calendy DB에서 삭제
                for (plan in originalPlanList) {
                    when (plan) {
                        is Schedule -> calendyDatabase.scheduleDao().delete(plan)
                        is Todo -> calendyDatabase.todoDao().delete(plan)
                    }
                }

                // TODO: Message DB에 Message 넣어주기
            }
        } catch (e: Throwable) {
            // TODO: Catching all throwable may not be good
            Log.e("GUN", e.stackTraceToString())
        }
    }

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