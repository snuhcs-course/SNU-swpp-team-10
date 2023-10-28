package com.example.calendy

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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.calendy.data.CalendyDatabase


@Composable
fun SqlExecutionTestPage(modifier: Modifier = Modifier) {
    val viewModel: SqlExecutionViewModel = viewModel(factory = AppViewModelProvider.Factory)
    var userInput by remember {
        mutableStateOf("DELETE FROM todo WHERE id=1")
    }
    
    Column {
        TextField(value = userInput, onValueChange = { userInput = it }, modifier = Modifier.fillMaxWidth())
        Button(onClick = { viewModel.sendQuery(userInput) }) {
            Text("Send")
        }
    }
}

class SqlExecutionViewModel(val calendyDatabase: CalendyDatabase): ViewModel() {
    fun sendQuery(queryString: String) {
        val supportDb: SupportSQLiteDatabase = calendyDatabase.openHelper.writableDatabase
        supportDb.execSQL(queryString)
    }
}