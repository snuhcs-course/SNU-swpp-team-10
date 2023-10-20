package com.example.calendy.view.editplanview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

@Composable

fun SetRepeat() {
    var isDialogOpen by remember { mutableStateOf(false) }
    var repeatIntervalText by remember { mutableStateOf("반복 안 함") }

    Button(onClick = { isDialogOpen = true }) {
        Text(text = repeatIntervalText)
    }

    if (isDialogOpen) {
        setRepeatDialog(onDismiss = { isDialogOpen = false }) { selectedRecurrence ->
            repeatIntervalText = selectedRecurrence
            isDialogOpen = false
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun setRepeatDialog(onDismiss: () -> Unit, onRecurrenceSelected: (String) -> Unit) {
    // repeatGroup table's repeatInt attribute value
    var repeatInt by remember { mutableStateOf("") }
    // repeatGroup table's repeatRule attribute value
    val repeatRadioGroup = remember { mutableStateOf("") }
    val durationRadioGroup = remember { mutableStateOf("") }
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn() {
                item {
                    TopAppBar(modifier = Modifier.fillMaxWidth(), navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = null)
                        }
                    }, title = { Text("반복") })
                }
                item {
                    Text(
                        text = "반복 선택",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Divider()
                }
                item { radioButtonLine("반복 안 함", repeatRadioGroup) }
                item { radioButtonLine("매일", repeatRadioGroup) }
                item { radioButtonLine("매주", repeatRadioGroup) }
                item { radioButtonLine("매월", repeatRadioGroup) }
                item { radioButtonLine("매년", repeatRadioGroup) }




                item {
                    Text(
                        text = "기간",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Divider()
                }
                item { radioButtonLine("계속 반복", durationRadioGroup) }
                item { radioButtonLine("반복 횟수", durationRadioGroup) }
                item { radioButtonLine("종료 날짜", durationRadioGroup) }


            }
        }
    }
}


@Composable
fun radioButtonLine(text: String, selectedText: MutableState<String>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selectedText.value==text, onClick = {
            selectedText.value = text
        })
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text)

        Spacer(modifier = Modifier.width(16.dp))

    }
}


@Preview(showBackground = true)
@Composable
fun SetRepeatPreview() {
    SetRepeat()
}
