package com.example.calendy.view.editplanview

import java.util.Date

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.update


class EditPlanViewModel : ViewModel() {

    // ViewModel 내에서만 uiState 수정 가능하도록 설정
    private val _uiState = MutableStateFlow(EditPlanUiState())
    val uiState: StateFlow<EditPlanUiState> = _uiState.asStateFlow()

    //fun setType

    fun setTitle(userInput: String) {
        _uiState.update {currentState -> currentState.copy(titleField = userInput)}
    }
    fun setMemo(userInput: String) {
        _uiState.update {currentState -> currentState.copy(memoField = userInput)}
    }
    //time picker
   // fun setStartTime
   // fun setEndTime

//    fun deletePlan(){
//        if() {
//            ScheduleRepositiry.delete()
//        } else TodoRepository.delete()
//    }
//    fun addPlan( )


}