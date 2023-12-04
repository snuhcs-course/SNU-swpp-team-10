package com.example.calendy.view.voiceAssistance

data class VoiceAssistanceUiState(
    val userInputText: String = "",
    val voiceListenerState: String = "",
    val isListening: Boolean = false,
)
