package com.example.calendy.view.messagepage

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calendy.data.log.ILogPlanRepository
import com.example.calendy.data.log.LogPlan
import com.example.calendy.data.log.LogSchedule
import com.example.calendy.data.log.LogTodo
import com.example.calendy.data.message.IMessageRepository
import com.example.calendy.data.message.Message
import com.example.calendy.data.plan.IPlanRepository
import com.example.calendy.data.plan.Plan
import com.example.calendy.data.plan.Schedule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import java.util.LinkedList

class MessagePlanLogViewModel(
    val messageRepository: IMessageRepository,
    val planRepository: IPlanRepository,
    val logPlanRepository: ILogPlanRepository
) : ViewModel(){

    // currently selected message to show plan logs
//    var selectedMessage = MutableStateFlow(Message(0,Date(), true, "", false))
    // modified plans
    var modifiedPlans = MutableStateFlow(emptyList<Plan>())

    // call on message show popup button clicked
    fun onMessageSelected(message: Message)
    {
//        selectedMessage.update { message }
        getModifiedPlanLogOfMessage(message)
    }

    private fun getModifiedPlanLogOfMessage(message: Message) {
        viewModelScope.launch {
            val targetPlans = logPlanRepository.getLogPlansByMessageId(message.id)
            val scheduleIDs: LinkedList<Int> = LinkedList()
            val todoIDs: LinkedList<Int> = LinkedList()
            targetPlans.map {
                if (it.planId != null)
                    when (it) {
                        is LogSchedule -> scheduleIDs.add(it.planId!!)
                        is LogTodo -> todoIDs.add(it.planId!!)
                    }
            }
            val modifiedResults = planRepository.getPlansByIds(scheduleIDs, todoIDs)
            modifiedPlans.update { modifiedResults }
        }
    }
}