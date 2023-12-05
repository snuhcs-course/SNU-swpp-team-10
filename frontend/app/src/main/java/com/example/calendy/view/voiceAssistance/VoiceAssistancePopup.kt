package com.example.calendy.view.voiceAssistance

import LoadingAnimation2
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.MoodBad
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.calendy.ui.theme.Blue3
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp

@Composable
fun VoiceAssistancePopup(
    viewModel: VoiceAssistanceViewModel,
    onDismissRequest :() -> Unit,
) {
    val context = LocalContext.current
    viewModel.startVoiceRecognition(context)

    Dialog(
        onDismissRequest = {
            onDismissRequest()
            viewModel.stopVoiceRecognition(context)
        },
//        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false) // prevent wrong exit from clicking outside
        // Dialog Properties
    ) {
        val uiState by viewModel.uiState.collectAsState()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = uiState.AiText,
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                color = Color(0xC0FFFFFF),
                modifier = Modifier.padding(bottom = 24.dp, top = 48.dp).fillMaxWidth()
            )

            // animation
            Box(
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                if(uiState.listenerState== VoiceAssistanceState.LISTENING)
                    LoadingAnimation2(
                        circleColor = Blue3,
                        circleSize = 16.dp,
                        spaceBetween = 16.dp,
                        animationDelay = 300,
                        initialAlpha = 0.5f
                    )
                else if (uiState.listenerState== VoiceAssistanceState.ERROR){
                    Icon(
                        imageVector = Icons.Filled.MoodBad,
                        contentDescription = "Bad",
                        tint = Color(0x40FFFFFF),
                        modifier = Modifier.size(48.dp)
                    )
                }
                else if(uiState.listenerState== VoiceAssistanceState.DONE){
                    Icon(
                        imageVector = Icons.Filled.CheckCircleOutline,
                        contentDescription = "Good",
                        tint = Color(0x40FFFFFF),
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            // user inputext

            Row (
                modifier = Modifier
                    .padding(top = 72.dp)
                    .wrapContentSize()
            ){
                Text(
                    text = "“ ",
                    textAlign = TextAlign.Center,
                    color = Color(0xC0FFFFFF),
                    fontSize= 28.sp,
                )
                Text(
                    text = uiState.userInputText,
                    textAlign = TextAlign.Center,
                    fontSize= 14.sp,
                    color = Color(0xC0FFFFFF),
                    modifier = Modifier
                        .padding(top = 12.dp)

                )
                Text(
                    text = " ”",
                    textAlign = TextAlign.Center,
                    color = Color(0xC0FFFFFF),
                    fontSize= 28.sp,
                )
            }



//            // cancel button
//            IconButton(
//                onClick = onDismissRequest,
//                modifier = Modifier
//                    .padding(vertical = 20.dp)
//
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Cancel,
//                    contentDescription = "Cancel",
//                    tint = Color(0x40FFFFFF),
//                    modifier = Modifier.size(48.dp)
//                )
//            }

        }
    }

}