package com.example.calendy

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calendy.view.messagepage.SqlExecutionViewModel


@Composable
fun SqlExecutionTestPage(modifier: Modifier = Modifier) {
    val viewModel: SqlExecutionViewModel = viewModel(factory = AppViewModelProvider.Factory)
    var userInput by remember {
//        mutableStateOf("내일 3시에 컴퓨터구조 시험 일정 추가해줘")
        mutableStateOf("컴퓨터구조 시험 일정을 11월 6일로 변경해줘")
//        mutableStateOf("컴퓨터구조 시험 일정 삭제해줘")
    }

    val context = LocalContext.current

    Column {
        TextField(
            value = userInput,
            onValueChange = { userInput = it },
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = { viewModel.sendQuery(userInput) }) {
            Text("Send")
        }
        Button(onClick = { viewModel.testLocal() }) {
            Text("Test Local")
        }
        Button(onClick = {
            val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            speechRecognizer.setRecognitionListener(viewModel.recognitionListener)
            speechRecognizer.startListening(
                Intent(
                    RecognizerIntent.ACTION_RECOGNIZE_SPEECH
                ).putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
            )
        }) {
            Icon(imageVector = Icons.Default.Mic, contentDescription = null)
        }

        val speechRecognizerText by viewModel.speechRecognizerState.collectAsState()
        Text(text = speechRecognizerText)
    }
}
