package com.swpp10.calendy.view.editplanview

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swpp10.calendy.data.maindb.category.Category
import com.swpp10.calendy.data.maindb.category.ICategoryRepository
import com.swpp10.calendy.data.maindb.plan.IPlanRepository
import com.swpp10.calendy.data.maindb.plan.Plan
import com.swpp10.calendy.data.maindb.plan.PlanType
import com.swpp10.calendy.data.maindb.plan.Schedule
import com.swpp10.calendy.data.maindb.plan.Todo
import com.swpp10.calendy.data.maindb.repeatgroup.IRepeatGroupRepository
import com.swpp10.calendy.data.maindb.repeatgroup.RepeatGroup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import kotlin.math.max
import kotlin.math.min

class EditPlanViewModel(
    private val planRepository: IPlanRepository,
    private val categoryRepository: ICategoryRepository,
    private val repeatGroupRepository: IRepeatGroupRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditPlanUiState(isAddPage = true))
    val uiState: StateFlow<EditPlanUiState> = _uiState.asStateFlow()

    // Will be shown in category popup
    val categoryListState = (categoryRepository.getCategoriesStream()).stateIn(
        scope = viewModelScope,
        initialValue = emptyList(),
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000)
    )


    // set once when navigating to EditPlanPage
    /**
     * @param id: null if new plan
     * @param type: PlanType.SCHEDULE or PlanType.TOD0
     * @param startDate: set startDate for new plan. null if existing plan
     * @param endDate: set endDate for new plan. if null for new plan, endDate = startDate
     */
    fun initialize(id: Int?, type: PlanType, startDate: Date?, endDate: Date?) {
        if (id==null) {
            // new plan
            // In Weekly Page: startDate.hour is important
            // In Monthly Page:
            // In TodoList Page: startDate.hour is set to current time
            val time = (startDate ?: Date()) //.applyTime(currentHour, currentMinute)
            val endTime = endDate ?: time // If not specified, set endTime to startTime

            _uiState.value = EditPlanUiState(
                isAddPage = true,
                entryType = type,
                startTime = time,
                endTime = endTime,
                dueTime = time
            )
        } else {
            // edit existing plan
            _uiState.value = EditPlanUiState(isAddPage = false, id = id, entryType = type)

            // fill in other values after db query
            viewModelScope.launch() {
                withContext(Dispatchers.IO) {
                    val plan = planRepository.getPlanById(id, type)

                    getCategoryUpdated(plan.categoryId)

                    _uiState.update {
                        fillIn(plan)
                    }
                }

            }
        }
    }

    private fun fillIn(plan: Plan?): EditPlanUiState {
        if (plan!=null) {
            val repeatGroup: RepeatGroup? = if (plan.repeatGroupId!=null) {
                repeatGroupRepository.getRepeatGroupById(plan.repeatGroupId!!)
            } else null

            return when (plan) {
                is Schedule -> {
                    _uiState.value.copy(
                        titleField = plan.title,
                        startTime = plan.startTime,
                        endTime = plan.endTime,
                        // Category is updated in getCategoryUpdated
                        repeatGroupId = plan.repeatGroupId,
                        repeatGroup = repeatGroup,
                        priority = plan.priority,
                        memoField = plan.memo,
                        showInMonthlyView = plan.showInMonthlyView
                    )
                }

                is Todo     -> {
                    _uiState.value.copy(
                        titleField = plan.title,
                        isComplete = plan.complete,
                        dueTime = plan.dueTime,
                        // Category is updated in getCategoryUpdated
                        repeatGroupId = plan.repeatGroupId,
                        repeatGroup = repeatGroup,
                        priority = plan.priority,
                        memoField = plan.memo,
                        showInMonthlyView = plan.showInMonthlyView
                    )
                }
            }
        } else {
            return _uiState.value.copy()
        }
    }


    //region Set UI State
    // Style: functions' order is aligned with UI
    fun setType(selectedType: PlanType) {
        _uiState.update { currentState -> currentState.copy(entryType = selectedType) }
    }


    fun setTitle(userInput: String) {
        _uiState.update { currentState -> currentState.copy(titleField = userInput) }
    }

    fun setIsComplete(isComplete: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(isComplete = isComplete)
        }
    }

    //region DateSelector
    // Invariance: startTime == dueTime (Imposed for addPlan & User Experience)
    fun setDueTime(inputDate: Date) {
        _uiState.update { currentState ->
            currentState.copy(
                dueTime = inputDate, startTime = inputDate, endTime = inputDate
            )
        }
    }

    fun setTimeRange(startDate: Date, endDate: Date) {
        if (startDate.before(endDate)) {
            // startDate < endDate
            _uiState.update { currentState ->
                currentState.copy(
                    startTime = startDate, endTime = endDate, dueTime = startDate
                )
            }
        } else {
            _uiState.update { currentState ->
                currentState.copy(
                    startTime = startDate, endTime = startDate, dueTime = startDate
                )
            }
        }
    }
    //endregion

    //region Category
    fun setCategory(category: Category?) {
        getCategoryUpdated(category?.id)
        if (category!=null) setPriority(category.defaultPriority)
    }

    private var job: Job? = null
    // Update category even when category is deleted
    private fun getCategoryUpdated(id: Int?) {
        job?.cancel()

        if (id==null || id <= 0) {
            _uiState.update { currentState -> currentState.copy(category = null) }
            return
        }

        val flow = categoryRepository.getCategoryStreamById(id)
        job = viewModelScope.launch {
            flow.collect {
                _uiState.update { currentState -> currentState.copy(category = it) }
            }
        }
    }

    fun addCategory(title: String, defaultPriority: Int) {
        viewModelScope.launch {
            categoryRepository.insert(Category(title = title, defaultPriority = defaultPriority))
        }
    }

    fun updateCategory(title: String, defaultPriority: Int, category: Category) {
        viewModelScope.launch {
            val updatedCategory = category.copy(
                title = title, defaultPriority = defaultPriority
            )
            categoryRepository.update(updatedCategory)
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            categoryRepository.delete(category)
        }
    }
    //endregion

    fun setRepeatGroup(repeatGroup: RepeatGroup?) {
        _uiState.update { currentState ->
            if (repeatGroup!=null) {
                // TODO: Is it valid to set repeatGroup.id?
                currentState.copy(repeatGroup = repeatGroup)
            } else {
                currentState.copy(repeatGroup = null)
            }
        }
    }

    fun setPriority(input: Int) {
        val priority = max(1, min(5, input))
        _uiState.update { currentState -> currentState.copy(priority = priority) }
    }

    fun setMemo(userInput: String) {
        _uiState.update { currentState -> currentState.copy(memoField = userInput) }
    }

    fun setShowInMonthlyView(showInMonthlyView: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(showInMonthlyView = showInMonthlyView)
        }
    }
    //endregion

    private fun generatePlanFromCurrentState(): Plan {
        val currentState = _uiState.value
        return when (currentState.entryType) {
            PlanType.SCHEDULE -> Schedule(
                title = currentState.titleField,
                startTime = currentState.startTime,
                endTime = currentState.endTime,
                memo = currentState.memoField,
                repeatGroupId = currentState.repeatGroup?.id,
                categoryId = currentState.category?.id,
                priority = currentState.priority,
                showInMonthlyView = currentState.showInMonthlyView,
                isOverridden = false
            )

            PlanType.TODO     -> Todo(
                title = currentState.titleField,
                dueTime = currentState.dueTime,
                memo = currentState.memoField,
                complete = currentState.isComplete,
                repeatGroupId = currentState.repeatGroup?.id,
                categoryId = currentState.category?.id,
                priority = currentState.priority,
                showInMonthlyView = currentState.showInMonthlyView,
                isOverridden = false
            )
        }
    }

    fun addPlan() {
        if (_uiState.value.titleField=="") {
            _uiState.update { currentState -> currentState.copy(titleField = "Untitled") }
        }
        val currentState = _uiState.value

        viewModelScope.launch(context = Dispatchers.IO) {
            val repeatGroup = currentState.repeatGroup
            if (repeatGroup==null) {
                val newPlan = generatePlanFromCurrentState()
                // Insert the plan into the database
                planRepository.insert(newPlan)
            } else {
                // TODO: Repeat Group Feature Give up
                // first insert current plan into db (This plan should not be visible in ui)
                // then insert repeat group
            }
        }
    }

    fun updatePlan() {
        if (_uiState.value.titleField=="") {
            _uiState.update { currentState -> currentState.copy(titleField = "Untitled") }
        }
        val currentState = _uiState.value

        if (currentState.id==null) {
            Log.e("Edit Plan View Model - updatePlan", "id should not be null")
            return
        }

        viewModelScope.launch(context = Dispatchers.IO) {
            if (currentState.repeatGroupId==null) {
                // id: Int? is smart casted into type Int
                val updatedPlan = when (val newPlan = generatePlanFromCurrentState()) {
                    is Schedule -> {
                        newPlan.copy(id = currentState.id)
                    }

                    is Todo     -> {
                        newPlan.copy(id = currentState.id)
                    }
                }

                planRepository.update(updatedPlan)
            } else {
                // TODO: Repeat Group Feature Give up
            }
        }

    }


    fun deletePlan() {
        val currentState = _uiState.value

        if (currentState.id==null) {
            Log.e("Edit Plan View Model - deletePlan", "id should not be null")
            return
        }

        viewModelScope.launch(context = Dispatchers.IO) {
            if (currentState.repeatGroupId==null) {
                val deletedPlan = when (currentState.entryType) {
                    PlanType.SCHEDULE -> Schedule(
                        id = currentState.id,
                        title = currentState.titleField,
                        startTime = currentState.startTime,
                        endTime = currentState.endTime,
                    )

                    PlanType.TODO     -> Todo(
                        id = currentState.id,
                        title = currentState.titleField,
                        dueTime = currentState.dueTime,
                    )
                }
                planRepository.delete(deletedPlan)
            } else {
                // TODO: Repeat Group Feature Give up
                repeatGroupRepository.deleteRepeatGroupById(currentState.repeatGroupId)
            }
        }
    }

}