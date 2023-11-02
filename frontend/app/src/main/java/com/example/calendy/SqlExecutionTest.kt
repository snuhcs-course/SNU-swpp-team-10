package com.example.calendy

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.calendy.data.CalendyDatabase
import com.example.calendy.data.emptydb.EmptyDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
fun SqlExecutionTestPage(modifier: Modifier = Modifier) {
    val viewModel: SqlExecutionViewModel = viewModel(factory = AppViewModelProvider.Factory)
    var userInput by remember {
        mutableStateOf("내일 3시에 컴퓨터구조 시험 일정 추가해줘")
    }

    Column {
        TextField(
            value = userInput,
            onValueChange = { userInput = it },
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = { viewModel.sendQuery(userInput) }) {
            Text("Send")
        }
    }
}

class SqlExecutionViewModel(
    val calendyDatabase: CalendyDatabase, val emptyDatabase: EmptyDatabase
) : ViewModel() {
    fun sendQuery(queryString: String) {
        viewModelScope.launch {
//            Log.d("GUN", "send to server $queryString")
//            val result = withContext(Dispatchers.IO) {
//                RetrofitClient.instance.sendMessageToServer(
//                    MessageBody(
//                        message = queryString
//                    )
//                )
//            }
//
//            val queries = result.queries
//            for (query in queries) {
//                sqlExecute(query)
//            }
            // TODO: Just for Test!!! should un-comment above
            withContext(Dispatchers.IO) {
                sqlExecute("INSERT INTO schedule (title, start_time, end_time, priority) VALUES ('컴퓨터구조 시험', datetime('now', '+1 day', '3 hours'), datetime('now', '+1 day', '4 hours'), 1)")
            }
        }
    }

    private suspend fun sqlExecute(sqlQuery: String) {
        Log.d("GUN", sqlQuery)
        // TODO: Not Tested. Help!
        // if isSchedule is false, should query tod0 db
        val isSchedule = sqlQuery.contains("from schedule", ignoreCase = true)
        val isInsert = sqlQuery.contains("INSERT", ignoreCase = true)
        val isUpdate = sqlQuery.contains("UPDATE", ignoreCase = true)
        val isDelete = sqlQuery.contains("DELETE", ignoreCase = true)

        emptyDatabase.deleteAll() // TODO: 실행이 언제 끝나는지 걱정해야 하나?

        try {
            if (isInsert) {
                // EmptyDB에 sqlQuery 실행 - emptyDB.query()
                emptyDatabase.openHelper.writableDatabase.execSQL(sqlQuery)
                // emptyDB에 Select All -> 결과
                val plans = emptyDatabase.getAllPlansStream().first()
                Log.d("GUN", plans.toString())
            } else if (isUpdate) {
                // SELECT where ... 로 교체
                // Calendy DB의 DAO.rawQuery 로 결과 받기
                // 결과를 Empty DB에 삽입
                // EmptyDB에 sqlQuery 실행 - emptyDB.query()
                // emptyDB에 Select All -> 결과
            } else if (isDelete) {
                // SELECT where ... 로 교체
                // Calendy DB의 DAO.rawQuery -> 결과
            }
        } catch (e: Throwable) {
            // TODO: Catching all throwable may not be good
            Log.e("GUN", e.stackTraceToString())
        }


        // 결과를 유저 검증

        val supportDb: SupportSQLiteDatabase = calendyDatabase.openHelper.writableDatabase
        supportDb.execSQL(sqlQuery)
    }
}