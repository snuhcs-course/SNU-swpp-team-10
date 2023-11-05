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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calendy.view.messagepage.SqlExecutionViewModel


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
