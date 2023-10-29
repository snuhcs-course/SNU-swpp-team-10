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
import com.example.calendy.data.MessageBody
import com.example.calendy.data.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
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

class SqlExecutionViewModel(val calendyDatabase: CalendyDatabase) : ViewModel() {
    fun sendQuery(queryString: String) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                RetrofitClient.instance.sendMessageToServer(
                    MessageBody(
                        message = queryString
                    )
                ).execute()
            }
            Log.d("GUN", result.toString())
            Log.d("GUN", result.body().toString())

            val queries = result.body()!!.queries
            Log.d("GUN", "Queries: $queries")
            for (query in queries) {
                sqlExecute(query)
            }
        }
    }

    fun sqlExecute(sqlQuery: String) {
        Log.d("GUN", sqlQuery)
        val supportDb: SupportSQLiteDatabase = calendyDatabase.openHelper.writableDatabase
        supportDb.execSQL(sqlQuery)
    }
}