package com.example.calendy.view.messageview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calendy.data.maindb.history.IHistoryRepository
import com.example.calendy.data.maindb.message.IMessageRepository
import com.example.calendy.data.maindb.message.Message
import com.example.calendy.data.maindb.plan.IPlanRepository
import com.example.calendy.data.maindb.plan.Plan
import com.example.calendy.data.maindb.plan.PlanType
import com.example.calendy.utils.getPlanType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    val modifiedPlanItems: MutableStateFlow<List<ModifiedPlanItem>> = MutableStateFlow(emptyList())

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


                val histories = historyRepository.getRevisionHistoriesByMessageId(message.id)
                val modifiedItems = histories.map { history ->
                    val savedPlanId = when (history.isSchedule) {
                        true  -> history.savedScheduleId
                        false -> history.savedTodoId
                    }
                    val currentPlanId = when (history.isSchedule) {
                        true  -> history.currentScheduleId
                        false -> history.currentTodoId
                    }
                    val planType = when (history.isSchedule) {
                        true  -> PlanType.SCHEDULE
                        false -> PlanType.TODO
                    }

                    val savedPlan: Plan? = savedPlanId?.let {
                        historyRepository.getSavedPlanById(savedPlanId,planType)
                    }
                    val currentPlan: Plan? = currentPlanId?.let {
                        planRepository.getPlanById(currentPlanId, planType)
                    }

                    ModifiedPlanItem(
                        history.id,
                        savedPlan,
                        currentPlan,
                        history.revisionType
                    )


                }
                modifiedPlanItems.update { modifiedItems }
            }
        }
    }

    fun undoModify(modifiedPlanItem: ModifiedPlanItem) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val savedPlan :Plan? = modifiedPlanItem.planBefore
                val currentPlan :Plan? = modifiedPlanItem.planAfter

                // 1. delete current plan from plan table (UPDATE or INSERT)
                // 2. insert saved plan to plan table (UPDATE or DELETE)
                // 3. delete saved plan from saved plan table (UPDATE or DELETE)
                // 4. delete history from history table (all)

                if(modifiedPlanItem.queryType == QueryType.UPDATE || modifiedPlanItem.queryType == QueryType.INSERT)
                    if (currentPlan!=null) planRepository.delete(currentPlan)

                if (modifiedPlanItem.queryType == QueryType.UPDATE || modifiedPlanItem.queryType == QueryType.DELETE){
                    if(savedPlan != null) planRepository.insert(savedPlan)
                    if(savedPlan != null) historyRepository.deleteSavedPlan(savedPlan)
                }


                historyRepository.deleteHistoryById(modifiedPlanItem.historyId)


            }
        }
    }
}