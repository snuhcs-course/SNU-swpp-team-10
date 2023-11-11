package com.example.calendy.view.editplanview

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calendy.data.category.Category
import com.example.calendy.data.category.ICategoryRepository
import com.example.calendy.data.plan.Plan
import com.example.calendy.data.plan.Plan.PlanType
import com.example.calendy.data.plan.Schedule
import com.example.calendy.data.plan.Todo
import com.example.calendy.data.plan.schedule.IScheduleRepository
import com.example.calendy.data.plan.todo.ITodoRepository
import com.example.calendy.data.repeatgroup.IRepeatGroupRepository
import com.example.calendy.data.repeatgroup.RepeatGroup
import com.example.calendy.utils.DateHelper.extract
import com.example.calendy.utils.DateHelper.getDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date
import kotlin.math.max
import kotlin.math.min

class EditPlanViewModel(
    private val scheduleRepository: IScheduleRepository,
    private val todoRepository: ITodoRepository,
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

            _uiState.value = EditPlanUiState(isAddPage = true, entryType = type, startTime = calendar.time, endTime = calendar.time, dueTime = calendar.time)
        } else {
            // edit existing plan
            // TODO: _uiState.value is set. but it is suspended because of db query
            // TODO: 임시 값 넣어놓기?
            _uiState.value = EditPlanUiState(isAddPage = false, id = id, entryType = type)

            // fill in other values after db query
            viewModelScope.launch {
                val plan = when (type) {
                    PlanType.Schedule -> {
                        scheduleRepository.getScheduleById(id)
                    }

                    PlanType.Todo     -> {
                        todoRepository.getTodoById(id)
                    }
                }.first()

                _uiState.value = fillIn(plan)
            }
        }
    }

    private suspend fun fillIn(plan: Plan?): EditPlanUiState {
       if(plan != null){
           val category: Category? = if (plan.categoryId!=null) {
               categoryRepository.getCategoryById(plan.categoryId!!).first()
           } else {
               null
           }

           var repeatGroup: RepeatGroup? = if(plan.repeatGroupId!=null) {
               repeatGroupRepository.getRepeatGroupById(plan.repeatGroupId!!).first()
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
                       priority = plan.priority!!,
                       memoField = plan.memo,
                       showInMonthlyView = plan.showInMonthlyView
                   )
               }

               is Todo     -> {
                   _uiState.value.copy(
                       titleField = plan.title,
                       isComplete = plan.complete,
                       isYearly = plan.yearly,
                       isMonthly = plan.monthly,
                       isDaily = plan.daily,
                       dueTime = plan.dueTime,
                       category = category,
                       repeatGroupId = plan.repeatGroupId,
                       repeatGroup = repeatGroup,
                       priority = plan.priority!!,
                       memoField = plan.memo,
                       showInMonthlyView = plan.showInMonthlyView
                   )
               }
           }
       } else {
          return  _uiState.value.copy()
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
    fun toggleIsYearly() {
        if (uiState.value.isYearly) {
            _uiState.update { currentState ->
                currentState.copy(
                    isYearly = false, isMonthly = false, isDaily = false
                )
            }
        } else {
            _uiState.update { currentState ->
                currentState.copy(
                    isYearly = true, isMonthly = false, isDaily = false
                )
            }
        }
    }

    fun toggleIsMonthly() {
        if (uiState.value.isMonthly) {
            _uiState.update { currentState ->
                currentState.copy(
                    isYearly = false, isMonthly = false, isDaily = false
                )
            }
        } else {
            _uiState.update { currentState ->
                currentState.copy(
                    isYearly = false, isMonthly = true, isDaily = false
                )
            }
        }
    }

    fun toggleIsDaily() {
        if (uiState.value.isDaily) {
            _uiState.update { currentState ->
                currentState.copy(
                    isYearly = false, isMonthly = false, isDaily = false
                )
            }
        } else {
            _uiState.update { currentState ->
                currentState.copy(
                    isYearly = false, isMonthly = false, isDaily = true
                )
            }
        }
    }

    fun setDueYear(newYear: Int) {
        val (_, monthZeroIndexed, day, hour, minute) = uiState.value.dueTime.extract()
        setDueTime(
            getDate(
                year = newYear,
                monthZeroIndexed = monthZeroIndexed,
                day = day,
                hourOfDay = hour,
                minute = minute,
                softWrap = true
            )
        )
    }

    fun setDueMonth(newYear: Int, newMonthZeroIndexed: Int) {
        val (_, _, day, hour, minute) = uiState.value.dueTime.extract()
        setDueTime(
            getDate(
                year = newYear,
                monthZeroIndexed = newMonthZeroIndexed,
                day = day,
                hourOfDay = hour,
                minute = minute,
                softWrap = true
            )
        )
    }

    fun setDueTime(inputDate: Date) {
        _uiState.update { currentState -> currentState.copy(dueTime = inputDate) }
    }

    fun setTimeRange(startDate: Date, endDate: Date) {
        _uiState.update { currentState ->
            currentState.copy(
                startTime = startDate, endTime = endDate
            )
        }
    }

    //endregion

    fun setCategory(category: Category?) {
        _uiState.update { currentState -> currentState.copy(category = category) }
        if(category!=null) setPriority(category.defaultPriority)
    }

    fun addCategory(title: String, defaultPriority: Int) {
        viewModelScope.launch {
            categoryRepository.insert(Category(title = title, defaultPriority = defaultPriority))
        }
    }

    //region Repeat Group
    fun setRepeatGroup(repeatGroup: RepeatGroup?) {
        _uiState.update { currentState ->
            if(repeatGroup != null) {
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
        if(_uiState.value.titleField ==""){
            _uiState.update { currentState -> currentState.copy(titleField = "Untitled") }
        }
        val currentState = _uiState.value
        when (currentState.entryType) {
            is PlanType.Schedule -> {
                val repeatGroup = currentState.repeatGroup
                if(repeatGroup!=null) {
                    viewModelScope.launch {
                        try {
                            // Switch to the IO dispatcher for database operations
                            withContext(Dispatchers.IO) {
                                val repeatGroupId: Int = repeatGroupRepository.insert(repeatGroup).toInt()
                                val endDate = repeatGroup.endDate
                                    ?: Date(currentState.endTime.time + 10L *365 * 24 * 60 * 60 * 1000) // 10 year later

                                var start = currentState.startTime
                                var end = currentState.endTime
                                if (repeatGroup.day) {
                                    while (end.before(endDate)) {
                                        val newSchedule = Schedule(
                                            title = currentState.titleField,
                                            startTime = start,
                                            endTime = end,
                                            memo = currentState.memoField,
                                            repeatGroupId = repeatGroupId,
                                            categoryId = currentState.category?.id,
                                            priority = currentState.priority,
                                            showInMonthlyView = currentState.showInMonthlyView,
                                            isOverridden = false
                                        )
                                        // Insert the schedule into the database
                                        scheduleRepository.insertSchedule(newSchedule)

                                        // Calculate the next start and end times
                                        start = Date(start.time + repeatGroup.repeatInterval * 24L * 60 * 60 * 1000)
                                        end = Date(end.time + repeatGroup.repeatInterval * 24L * 60 * 60 * 1000)
                                    }
                                } else if(repeatGroup.week) {
                                    val repeatDays = parseRepeatDays(repeatGroup.repeatRule ?: "")
                                    val calendar = Calendar.getInstance()
                                    val initialStart = start
                                    while(end.before(endDate)) {
                                        for (repeatDay in repeatDays) {
                                            calendar.time = start
                                            calendar.set(Calendar.DAY_OF_WEEK, repeatDay)
                                            if (calendar.time.before(initialStart)) continue
                                            val newStart = calendar.time
                                            val newEnd = Date(newStart.time + end.time - start.time)
                                            if (newEnd.after(endDate)) continue
                                            // Create and insert new schedule
                                            val newSchedule = Schedule(
                                                title = currentState.titleField,
                                                startTime = newStart,
                                                endTime = newEnd,
                                                memo = currentState.memoField,
                                                repeatGroupId = repeatGroupId,
                                                categoryId = currentState.category?.id,
                                                priority = currentState.priority,
                                                showInMonthlyView = currentState.showInMonthlyView,
                                                isOverridden = false
                                            )
                                            scheduleRepository.insertSchedule(newSchedule)
                                        }
                                        // Move to the next interval
                                        start = Date(start.time + repeatGroup.repeatInterval * 7L * 24 * 60 * 60 * 1000)
                                        end = Date(end.time + repeatGroup.repeatInterval * 7L * 24 * 60 * 60 * 1000)
                                    }
                                } else if(repeatGroup.month){
                                    val repeatDates = repeatGroup.repeatRule?.chunked(2)?.map { it.toInt() } ?: listOf()
                                    val calendar = Calendar.getInstance()
                                    val initialStart = start
                                    val duration = end.time - start.time

                                    while (end.before(endDate)) {
                                        for (dayOfMonth in repeatDates) {
                                            calendar.time = start
                                            // Ensure the dayOfMonth is within the current month's maximum day
                                            val maxDayInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                                            if (dayOfMonth <= maxDayInMonth) {
                                                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                                val newStart = calendar.time
                                                val newEnd = Date(newStart.time + duration)

                                                if (newEnd.before(endDate) && newStart.after(initialStart)) {
                                                    // Create and insert new schedule
                                                    val newSchedule = Schedule(
                                                        title = currentState.titleField,
                                                        startTime = newStart,
                                                        endTime = newEnd,
                                                        memo = currentState.memoField,
                                                        repeatGroupId = repeatGroupId,
                                                        categoryId = currentState.category?.id,
                                                        priority = currentState.priority,
                                                        showInMonthlyView = currentState.showInMonthlyView,
                                                        isOverridden = false
                                                    )
                                                    // Insert the schedule into the database
                                                    scheduleRepository.insertSchedule(newSchedule)
                                                }
                                            }
                                        }
                                        // Increment start date by one month
                                        calendar.add(Calendar.MONTH, repeatGroup.repeatInterval)
                                        calendar.set(Calendar.DAY_OF_MONTH, 1)
                                        start = calendar.time
                                        end = Date(start.time + duration)
                                    }
                                } else { //repeatGroup.year
                                    val newSchedule = Schedule(
                                        title = currentState.titleField,
                                        startTime = start,
                                        endTime = end,
                                        memo = currentState.memoField,
                                        repeatGroupId = repeatGroupId,
                                        categoryId = currentState.category?.id,
                                        priority = currentState.priority,
                                        showInMonthlyView = currentState.showInMonthlyView,
                                        isOverridden = false
                                    )
                                    // Insert the schedule into the database
                                    scheduleRepository.insertSchedule(newSchedule)

                                    // Calculate the next start and end times
                                    start = Date(start.time + repeatGroup.repeatInterval *365L* 24 * 60 * 60 * 1000)
                                    end = Date(end.time + repeatGroup.repeatInterval *365L* 24L * 60 * 60 * 1000)
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("AddPlan", "Error adding plan: ${e.localizedMessage}")
                        }
                    }

                } else {
                    val newSchedule = Schedule(
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
                    viewModelScope.launch { scheduleRepository.insertSchedule(newSchedule) }
                }

            }

            is PlanType.Todo     -> {
                val repeatGroup = currentState.repeatGroup
                if(repeatGroup!=null) {
                    viewModelScope.launch{
                        try{
                            withContext(Dispatchers.IO) {
                                val repeatGroupId: Int = repeatGroupRepository.insert(repeatGroup).toInt()
                                val endDate = repeatGroup.endDate ?: Date(currentState.dueTime.time + 10L * 365 * 24 * 60 * 60 * 1000) // 1 year later
                                var dueTime = currentState.dueTime
                                if(repeatGroup.day) {
                                    while(dueTime < endDate) {
                                        val newTodo = Todo(
                                            title = currentState.titleField,
                                            dueTime = dueTime,
                                            yearly = currentState.isYearly,
                                            monthly = currentState.isMonthly,
                                            daily = currentState.isDaily,
                                            memo = currentState.memoField,
                                            complete = currentState.isComplete,
                                            repeatGroupId = repeatGroupId,
                                            categoryId = currentState.category?.id,
                                            priority = currentState.priority,
                                            showInMonthlyView = currentState.showInMonthlyView,
                                            isOverridden = false
                                        )
                                        todoRepository.insertTodo(newTodo)
                                        dueTime = Date(dueTime.time + repeatGroup.repeatInterval*24L*60*60*1000)
                                    }
                                } else if(repeatGroup.week) {
                                    val repeatDays = parseRepeatDays(repeatGroup.repeatRule ?: "")
                                    val calendar = Calendar.getInstance()
                                    val initialDue = dueTime
                                    while(dueTime.before(endDate)) {
                                        for (repeatDay in repeatDays) {
                                            calendar.time = dueTime
                                            calendar.set(Calendar.DAY_OF_WEEK, repeatDay)
                                            if (calendar.time.before(initialDue) || calendar.time.after(endDate)) continue
                                            val newDue = calendar.time
                                            // Create and insert new schedule
                                            val newTodo = Todo(
                                                title = currentState.titleField,
                                                dueTime = newDue,
                                                yearly = currentState.isYearly,
                                                monthly = currentState.isMonthly,
                                                daily = currentState.isDaily,
                                                memo = currentState.memoField,
                                                complete = currentState.isComplete,
                                                repeatGroupId = repeatGroupId,
                                                categoryId = currentState.category?.id,
                                                priority = currentState.priority,
                                                showInMonthlyView = currentState.showInMonthlyView,
                                                isOverridden = false
                                            )
                                            todoRepository.insertTodo(newTodo)
                                        }
                                        // Move to the next interval
                                        dueTime = Date(dueTime.time + repeatGroup.repeatInterval * 7L * 24 * 60 * 60 * 1000)
                                    }
                                } else if(repeatGroup.month) {
                                    val repeatDates = repeatGroup.repeatRule?.chunked(2)?.map { it.toInt() } ?: listOf()
                                    val calendar = Calendar.getInstance()
                                    val initialDue = dueTime

                                    while (dueTime.before(endDate)) {
                                        for (dayOfMonth in repeatDates) {
                                            calendar.time = dueTime
                                            // Ensure the dayOfMonth is within the current month's maximum day
                                            val maxDayInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                                            if (dayOfMonth <= maxDayInMonth) {
                                                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                                val newDue = calendar.time

                                                if (newDue.before(endDate) && newDue.after(initialDue)) {
                                                    // Create and insert new todo
                                                    val newTodo = Todo(
                                                        title = currentState.titleField,
                                                        dueTime = newDue,
                                                        yearly = currentState.isYearly,
                                                        monthly = currentState.isMonthly,
                                                        daily = currentState.isDaily,
                                                        memo = currentState.memoField,
                                                        complete = currentState.isComplete,
                                                        repeatGroupId = repeatGroupId,
                                                        categoryId = currentState.category?.id,
                                                        priority = currentState.priority,
                                                        showInMonthlyView = currentState.showInMonthlyView,
                                                        isOverridden = false
                                                    )
                                                    todoRepository.insertTodo(newTodo)
                                                }
                                            }
                                        }
                                        // Increment due date by one month
                                        calendar.add(Calendar.MONTH, repeatGroup.repeatInterval)
                                        calendar.set(Calendar.DAY_OF_MONTH, 1)
                                        dueTime = calendar.time
                                    }
                                } else { //repeatGroup.year
                                    while (dueTime < endDate) {
                                        val newTodo = Todo(
                                            title = currentState.titleField,
                                            dueTime = dueTime,
                                            yearly = currentState.isYearly,
                                            monthly = currentState.isMonthly,
                                            daily = currentState.isDaily,
                                            memo = currentState.memoField,
                                            complete = currentState.isComplete,
                                            repeatGroupId = repeatGroupId,
                                            categoryId = currentState.category?.id,
                                            priority = currentState.priority,
                                            showInMonthlyView = currentState.showInMonthlyView,
                                            isOverridden = false
                                        )
                                        todoRepository.insertTodo(newTodo)
                                        dueTime = Date(dueTime.time + repeatGroup.repeatInterval * 365L * 24 * 60 * 60 * 1000)
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("AddPlan", "Error adding plan: ${e.localizedMessage}")
                        }
                    }
                } else {
                    val newTodo = Todo(
                        title = currentState.titleField,
                        dueTime = currentState.dueTime,
                        yearly = currentState.isYearly,
                        monthly = currentState.isMonthly,
                        daily = currentState.isDaily,
                        memo = currentState.memoField,
                        complete = currentState.isComplete,
                        repeatGroupId = null,
                        categoryId = currentState.category?.id,
                        priority = currentState.priority,
                        showInMonthlyView = currentState.showInMonthlyView,
                        isOverridden = false
                    )
                    viewModelScope.launch { todoRepository.insertTodo(newTodo) }
                }

            }
        }
    }

    fun updatePlan() {
        if(_uiState.value.titleField ==""){
            _uiState.update { currentState -> currentState.copy(titleField = "Untitled") }
        }
        val currentState = _uiState.value

        if (currentState.id==null) {
            // TODO: Report Error
            Log.e("GUN", "id should not be null")
            return
        }

        if(currentState.repeatGroupId != null) {
            deletePlan()
            addPlan()
        } else {
            // id: Int? is smart casted into type Int
            when (currentState.entryType) {
                is PlanType.Schedule -> {
                    val updatedSchedule = Schedule(
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
                    viewModelScope.launch { scheduleRepository.updateSchedule(updatedSchedule) }
                }

                is PlanType.Todo     -> {
                    val updatedTodo = Todo(
                        id = currentState.id,
                        title = currentState.titleField,
                        dueTime = currentState.dueTime,
                        yearly = currentState.isYearly,
                        monthly = currentState.isMonthly,
                        daily = currentState.isDaily,
                        memo = currentState.memoField,
                        complete = currentState.isComplete,
                        repeatGroupId = currentState.repeatGroup?.id,
                        categoryId = currentState.category?.id,
                        priority = currentState.priority,
                        showInMonthlyView = currentState.showInMonthlyView,
                        isOverridden = false
                    )
                    viewModelScope.launch { todoRepository.updateTodo(updatedTodo) }
                }
            }
        }

    }


    fun deletePlan() {
        val currentState = _uiState.value

        if (currentState.id==null) {
            // TODO: Report Error
            Log.e("GUN", "id should not be null")
            return
        }
        viewModelScope.launch {
            if(currentState.repeatGroupId != null) {
                repeatGroupRepository.deleteRepeatGroupById(currentState.repeatGroupId)
            } else {
                // id: Int? is smart casted into type Int
                when (currentState.entryType) {
                    is PlanType.Schedule -> {

                        val deletedSchedule = Schedule(
                            id = currentState.id,
                            title = currentState.titleField,
                            startTime = currentState.startTime,
                            endTime = currentState.endTime,
                        )
                        scheduleRepository.deleteSchedule(deletedSchedule)
                    }

                    is PlanType.Todo     -> {
                        val deletedTodo = Todo(
                            id = currentState.id,
                            title = currentState.titleField,
                            dueTime = currentState.dueTime,
                        )
                        todoRepository.deleteTodo(deletedTodo)
                    }
                }
            }

        }


    }

    private fun parseRepeatDays(repeatRule: String): List<Int> {
        val daysOfWeek = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")

        return daysOfWeek.mapIndexedNotNull { index, day ->
            if (repeatRule.contains(day)) {
                val calendarDay = index + 2
                if (calendarDay > 7) 1 else calendarDay
            } else null
        }
    }
}