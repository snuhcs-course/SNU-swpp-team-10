package com.example.calendy.view.editplanview

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calendy.data.maindb.category.Category
import com.example.calendy.data.maindb.category.ICategoryRepository
import com.example.calendy.data.maindb.plan.IPlanRepository
import com.example.calendy.data.maindb.plan.Plan
import com.example.calendy.data.maindb.plan.PlanType
import com.example.calendy.data.maindb.plan.Schedule
import com.example.calendy.data.maindb.plan.Todo
import com.example.calendy.data.maindb.repeatgroup.IRepeatGroupRepository
import com.example.calendy.data.maindb.repeatgroup.RepeatGroup
import com.example.calendy.utils.DateHelper.extract
import com.example.calendy.utils.applyTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
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


    val categoryListState = (categoryRepository.getCategoriesStream()).stateIn(
        scope = viewModelScope,
        initialValue = emptyList(),
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000)
    )


    // set once when navigating to EditPlanPage
    fun initialize(id: Int?, type: PlanType, date: Date?) {
        if (id==null) {
            // new plan
            val calendar = Calendar.getInstance()
            val providedCalendar = Calendar.getInstance().apply {
                time = date ?: Date()
            }
            calendar.set(Calendar.YEAR, providedCalendar.get(Calendar.YEAR))
            calendar.set(Calendar.MONTH, providedCalendar.get(Calendar.MONTH))
            calendar.set(Calendar.DAY_OF_MONTH, providedCalendar.get(Calendar.DAY_OF_MONTH))
            calendar.set(Calendar.HOUR_OF_DAY, providedCalendar.get(Calendar.HOUR_OF_DAY))
            calendar.set(Calendar.MINUTE, providedCalendar.get(Calendar.MINUTE))

            _uiState.value = EditPlanUiState(
                isAddPage = true,
                entryType = type,
                startTime = calendar.time,
                endTime = calendar.time,
                dueTime = calendar.time
            )
        } else {
            // edit existing plan
            _uiState.value = EditPlanUiState(isAddPage = false, id = id, entryType = type)

            // fill in other values after db query
            viewModelScope.launch() {
                withContext(Dispatchers.IO) {
                    val plan = planRepository.getPlanById(id, type)
                    _uiState.update {
                        fillIn(plan)
                    }
                }

            }
        }
    }

    private fun fillIn(plan: Plan?): EditPlanUiState {
        if (plan!=null) {
            val category: Category? = if (plan.categoryId!=null) {
                categoryRepository.getCategoryById(plan.categoryId!!)
            } else {
                null
            }

            val repeatGroup: RepeatGroup? = if (plan.repeatGroupId!=null) {
                repeatGroupRepository.getRepeatGroupById(plan.repeatGroupId!!)
            } else null

            return when (plan) {
                is Schedule -> {
                    _uiState.value.copy(
                        titleField = plan.title,
                        startTime = plan.startTime,
                        endTime = plan.endTime,
                        category = category,
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
                        category = category,
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

    fun setCategory(category: Category?) {
        _uiState.update { currentState -> currentState.copy(category = category) }
        if (category!=null) setPriority(category.defaultPriority)
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

    //region Repeat Group
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
    // endregion

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


    fun addPlan() {
        if (_uiState.value.titleField=="") {
            _uiState.update { currentState -> currentState.copy(titleField = "Untitled") }
        }
        val currentState = _uiState.value

        // GlobalScope may be bad!
        GlobalScope.launch(context = Dispatchers.IO) {
            val repeatGroup = currentState.repeatGroup
            if (repeatGroup==null) {
                val newPlan = when (currentState.entryType) {
                    PlanType.SCHEDULE -> Schedule(
                        title = currentState.titleField,
                        startTime = currentState.startTime,
                        endTime = currentState.endTime,
                        memo = currentState.memoField,
                        repeatGroupId = null,
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
                        repeatGroupId = null,
                        categoryId = currentState.category?.id,
                        priority = currentState.priority,
                        showInMonthlyView = currentState.showInMonthlyView,
                        isOverridden = false
                    )
                }
                // Insert the plan into the database
                planRepository.insert(newPlan)
            } else {
                try {
                    val repeatGroupId: Int = repeatGroupRepository.insert(repeatGroup).toInt()

                    // Invariance: startTime == dueTime (Imposed by setDueTime, setTimeRange)
                    val scheduleDuration = currentState.endTime.time - currentState.startTime.time
                    val (_, _, _, hour, minute) = currentState.startTime.extract()

                    var previousDate = currentState.startTime // TODO: TESTING

                    for (startDateOnly in repeatGroup.toIterable(currentState.startTime)) {
                        // Iterator returns Date Only without time information.
                        val repeatedDate = startDateOnly.applyTime(hour, minute)

                        if (!(previousDate < repeatedDate)) {
                            Log.e("GUN EditPlanViewModel", "Previous: $previousDate -> Current: $repeatedDate")
                        } else if (repeatedDate.hours != hour || repeatedDate.minutes != minute) {
                            Log.e("GUN EditPlanViewModel", "Current: $repeatedDate")
                        }
                        previousDate = repeatedDate

                        val newPlan = when (currentState.entryType) {
                            PlanType.SCHEDULE -> Schedule(
                                title = currentState.titleField,
                                startTime = repeatedDate,
                                endTime = Date(repeatedDate.time + scheduleDuration),
                                memo = currentState.memoField,
                                repeatGroupId = repeatGroupId,
                                categoryId = currentState.category?.id,
                                priority = currentState.priority,
                                showInMonthlyView = currentState.showInMonthlyView,
                                isOverridden = false
                            )

                            PlanType.TODO     -> Todo(
                                title = currentState.titleField,
                                dueTime = repeatedDate,
                                memo = currentState.memoField,
                                complete = currentState.isComplete,
                                repeatGroupId = repeatGroupId,
                                categoryId = currentState.category?.id,
                                priority = currentState.priority,
                                showInMonthlyView = currentState.showInMonthlyView,
                                isOverridden = false
                            )
                        }
                        Log.d("AddPlan", "newPlan: $newPlan")
                        planRepository.insert(newPlan)
                    }
                } catch (e: Exception) {
                    Log.e("AddPlan", "Error adding plan: ${e.localizedMessage}")
                    Log.e("AddPlan", e.stackTraceToString())
                }
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

        GlobalScope.launch(context = Dispatchers.IO) {
            if (currentState.repeatGroupId!=null) {
                deletePlan()
                // TODO: Start Date가 달라져버려서 망한다.
                addPlan()
            } else {
                // id: Int? is smart casted into type Int
                val updatedPlan = when (currentState.entryType) {
                    PlanType.SCHEDULE -> Schedule(
                        id = currentState.id,
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
                        id = currentState.id,
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
                planRepository.update(updatedPlan)
            }
        }

    }


    fun deletePlan() {
        val currentState = _uiState.value

        if (currentState.id==null) {
            Log.e("Edit Plan View Model - deletePlan", "id should not be null")
            return
        }

        GlobalScope.launch(context = Dispatchers.IO) {
            if (currentState.repeatGroupId!=null) {
                // Delete repeat group -> cascade delete
                repeatGroupRepository.deleteRepeatGroupById(currentState.repeatGroupId)
            } else {
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
            }
        }
    }

}