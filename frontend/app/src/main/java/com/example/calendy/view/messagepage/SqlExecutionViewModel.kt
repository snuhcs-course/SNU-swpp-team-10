package com.example.calendy.view.messagepage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.calendy.data.CalendyDatabase
import com.example.calendy.data.MessageBody
import com.example.calendy.data.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SqlExecutionViewModel(val calendyDatabase: CalendyDatabase) : ViewModel() {
    fun sendQuery(queryString: String) {
        viewModelScope.launch {
            Log.d("GUN", "send to server $queryString")
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