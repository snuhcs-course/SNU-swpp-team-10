package com.example.calendy.view.messagepage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calendy.data.maindb.history.IHistoryRepository
import com.example.calendy.data.maindb.message.IMessageRepository
import com.example.calendy.data.maindb.message.Message
import com.example.calendy.data.maindb.plan.IPlanRepository
import com.example.calendy.data.maindb.plan.Plan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.LinkedList

class MessagePlanLogViewModel(
    val messageRepository: IMessageRepository,
    val planRepository: IPlanRepository,
    val historyRepository: IHistoryRepository
) : ViewModel() {

    // currently selected message to show plan logs
//    var selectedMessage = MutableStateFlow(Message(0,Date(), true, "", false))
    // modified plans
    var modifiedPlans = MutableStateFlow(emptyList<Plan>())
    val modifiedPlanList: MutableStateFlow<List<Pair<Plan?, Plan?>>> = MutableStateFlow(emptyList())

    // call on message show popup button clicked
    fun onMessageSelected(message: Message) {
//        selectedMessage.update { message }
        getModifiedPlanLogOfMessage(message)
    }

    private fun getModifiedPlanLogOfMessage(message: Message) {
        viewModelScope.launch {
            // Cannot access database on the main thread since it may potentially lock the UI for a long period of time.
            withContext(Dispatchers.IO) {
                // TODO: Pair<Plan?. Plan?>으로 반환하기
//                val scheduleIDs: LinkedList<Int> = LinkedList()
//                val todoIDs: LinkedList<Int> = LinkedList()
//
//                val historyList = historyRepository.getSavedPlansByMessageId(message.id)
//                historyList.forEach {
//                    when (it.isSchedule) {
//                        // TODO: This May be null!
//                        true  -> scheduleIDs.add(it.currentScheduleId!!)
//                        false -> todoIDs.add(it.currentTodoId!!)
//                    }
//                }
//
//                val modifiedResults = planRepository.getPlansByIds(scheduleIDs, todoIDs)
//                modifiedPlans.update { modifiedResults }

                val revisionList = historyRepository.getRevisionHistoriesByMessageId(message.id)
                val modifiedList = revisionList.map { (planType, idPair) ->
                    val (savedPlanId, currentPlanId) = idPair

                    val savedPlan: Plan? = savedPlanId?.let {
                        historyRepository.getSavedPlanById(savedPlanId, planType)
                    }
                    val currentPlan: Plan? = currentPlanId?.let {
                        planRepository.getPlanById(currentPlanId, planType)
                    }

                    Pair(savedPlan, currentPlan)
                }
                modifiedPlanList.update { modifiedList }
            }
        }
    }
}