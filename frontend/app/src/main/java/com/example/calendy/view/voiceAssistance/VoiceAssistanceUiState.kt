package com.example.calendy.view.voiceAssistance

data class VoiceAssistanceUiState(
    val userInputText: String = "",
    val AiText: String = "",
    val listenerState: VoiceAssistanceState = VoiceAssistanceState.LISTENING,
)
