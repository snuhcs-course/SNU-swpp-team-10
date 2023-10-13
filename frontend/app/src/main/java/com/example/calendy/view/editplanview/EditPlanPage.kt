package com.example.calendy.view.editplanview

import android.widget.Button
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun EditPlanPage(editPlanViewModel: EditPlanViewModel = viewModel()) {
    val editPlanUiState by editPlanViewModel.uiState.collectAsState()
    var selectedButton by remember { mutableStateOf("일정") }
    Column(
            modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
    ) {
        // Top Bar
        TopAppBar()

        Row(
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center
        ) {
            Button(
                    onClick = { selectedButton = "일정" },
                    colors = ButtonDefaults.buttonColors(Color.Gray)

            ) {
                Text(text = "일정")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                    onClick = { selectedButton = "TODO" },
                    Modifier.background(Color(0x11111111))
            ) {
                Text(text = "TODO")
            }
        }
        TextField(value = "Todo 제목", onValueChange = { /* TODO: Handle text input */ })

        Spacer(modifier = Modifier.height(32.dp))

        // Date Selector
        DateSelector()

        TextField(value = "반복안함", onValueChange = { /* TODO: Handle text input */ })
        TextField(value = "category", onValueChange = { /* TODO: Handle text input */ })
        Spacer(modifier = Modifier.height(32.dp))

        // Category
        StarRating()

        TextField(value = "메모", onValueChange = { /* TODO: Handle text input */ })
        Spacer(modifier = Modifier.height(32.dp))

        // Submit Button
        Button(onClick = { /* TODO: Handle on submit */ }) {
            Text(text = "Done")
        }
    }
}

@Composable
fun TopAppBar() {
    Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")

    }
}

@Composable
fun DateSelector() {
    Column {
        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
        ) {
            // You can use 'RadioButton' for Yearly, Monthly, Daily
            // Example for Yearly
            RadioButton(selected = true /*Or false*/, onClick = { /* TODO: Handle yearly selection */ })
            Text(text = "Yearly")
        }

        Text(text = "2023년 10월 19일 (목) 오전 9:00")
    }
}

@Composable
fun StarRating() {
    Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
    ) {
        // You can use 'Icon' for each star and change its tint based on selection
        // Example for one star
        Icon(imageVector = Icons.Default.Star, contentDescription = "Star", tint = Color.Yellow)
        Icon(imageVector = Icons.Default.Star, contentDescription = "Star", tint = Color.Yellow)
        Icon(imageVector = Icons.Default.Star, contentDescription = "Star", tint = Color.Yellow)
        Icon(imageVector = Icons.Default.Star, contentDescription = "Star", tint = Color.Yellow)
        Icon(imageVector = Icons.Default.Star, contentDescription = "Star", tint = Color.Yellow)
    }
}

@Preview(showBackground = true, name = "Todo Screen Preview")
@Composable
fun TodoScreenPreview() {
    EditPlanPage()
}

