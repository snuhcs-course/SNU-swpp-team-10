package com.example.calendy.view.monthlyview

import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.calendy.AppViewModelProvider
import com.example.calendy.R
import com.example.calendy.data.dummy.DummyPlanRepository
import com.example.calendy.data.maindb.plan.IPlanRepository
import com.example.calendy.data.maindb.plan.Plan
import com.example.calendy.data.maindb.plan.PlanType
import com.example.calendy.data.maindb.plan.Schedule
import com.example.calendy.data.maindb.plan.Todo
import com.example.calendy.utils.afterDays
import com.example.calendy.utils.equalDay
import com.example.calendy.utils.toCalendarDay
import com.example.calendy.utils.toDate
import com.example.calendy.view.monthlyview.decorator.OneDayDecorator
import com.example.calendy.view.monthlyview.decorator.SaturdayDecorator
import com.example.calendy.view.monthlyview.decorator.SelectedDayDecorator
import com.example.calendy.view.monthlyview.decorator.SundayDecorator
import com.example.calendy.view.monthlyview.decorator.TitleDecorator
import com.example.calendy.view.popup.AddButton
import com.example.calendy.view.popup.EditButton
import com.example.calendy.view.popup.PlanDetailPopup
import com.example.calendy.view.popup.PopupHeaderDate
import com.example.calendy.view.popup.PopupHeaderTitle
import com.example.calendy.view.popup.SwitchablePlanListPopup
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter
import com.prolificinteractive.materialcalendarview.format.MonthArrayTitleFormatter
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import java.util.Date
import java.util.Hashtable

@Composable
fun MonthlyPageKT(
    monthlyViewModel: MonthlyViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onNavigateToEditPage: (id: Int?, type: PlanType, date: Date?) -> Unit
) {

    val custom_months = stringArrayResource(id = R.array.custom_months)
    val custom_weekdays = stringArrayResource(id = R.array.custom_weekdays)
    val uiState:MonthlyPageUIState by monthlyViewModel.uiState.collectAsState()

    var selectedDayDecorator: SelectedDayDecorator
    var saturdayDecorator : SaturdayDecorator = SaturdayDecorator(uiState.currentMonth.month)
    var sundayDecorator : SundayDecorator = SundayDecorator(uiState.currentMonth.month)

    var planOfMonth: Hashtable<CalendarDay, List<Plan>> = planListToHash(uiState.plansOfMonth)

    var popupDate by remember { mutableStateOf(uiState.selectedDate) }
    var popupPlan : Plan by remember { mutableStateOf(Schedule(-1, "", Date(), Date()))}
    var showListPopup by remember { mutableStateOf(false) }
    var showDetailPopup by remember { mutableStateOf(false) }


    fun openListPopup(selectedDate: CalendarDay)
    {
        popupDate=selectedDate
        showListPopup = true
    }
    fun openDetailPopup(plan: Plan)
    {
        popupPlan=plan
        showDetailPopup=true
    }
    fun openAddPlanPopup(selectedDate: CalendarDay)
    {
        onNavigateToEditPage(null, PlanType.SCHEDULE, selectedDate.toDate())
    }

    fun onListPopupDismissed()
    {
        showListPopup=false
//        popupDate=uiState.selectedDate
    }
    fun onDetailPopupDismissed()
    {
        showDetailPopup=false
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {


        AndroidView(modifier = Modifier.fillMaxSize(), factory = { context ->
            val calendar = MaterialCalendarView(context)
            calendar.apply {

                // initial setting for calendar view
                setTitleFormatter(MonthArrayTitleFormatter(custom_months))
                setWeekDayFormatter(ArrayWeekDayFormatter(custom_weekdays))
                setTileHeightDp(-1)
                selectionColor = -1
                setSelectedDate(uiState.selectedDate)
                showOtherDates = MaterialCalendarView.SHOW_OTHER_MONTHS
                state().edit()
                    .setFirstDayOfWeek(Calendar.SUNDAY)
                    .setMinimumDate(CalendarDay.from(2000, 0, 1))   //from 2000.1.1
                    .setMaximumDate(CalendarDay.from(2030, 11, 31)) //to 2030.12.31
                    .setCalendarDisplayMode(CalendarMode.MONTHS)
                    .commit();


                setOnDateChangedListener { widget, date, selected ->

                    if (planOfMonth.size == 0) planOfMonth = planListToHash(uiState.plansOfMonth)

                    if (date == uiState.selectedDate) {
                        openListPopup(date)
//                    openAddPlanPopup(date)
                    } else {
                        monthlyViewModel.setSelectedDate(date)
                    }
                }
                // event
                setOnMonthChangedListener(
                    // selected month changed
                    { widget, date ->
                        //TODO: change planList
                        monthlyViewModel.setCurrentMonth(date)
                    })

                Log.d("BANG", "mcv initialize")
            }
        },
            update =
            { mcv ->

                // selected day decorator initialization
                mcv.removeDecorators()
                selectedDayDecorator = SelectedDayDecorator(uiState.selectedDate, mcv.context)
                mcv.addDecorators(
                    saturdayDecorator,
                    sundayDecorator,
                    OneDayDecorator(),
                    selectedDayDecorator
                )
                for (p in planOfMonth) {
                    mcv.addDecorator(TitleDecorator(p.key, p.value))
                }
                Log.d("BANG", "mcv redraw")
            }
        )

        //add button
        AddButton(
            onButtonClick = {onNavigateToEditPage(null, PlanType.SCHEDULE,popupDate.toDate())},
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd)
        )
    }
        //list popup
    if (showListPopup && !showDetailPopup) {
        val planList = planOfMonth[popupDate]
        SwitchablePlanListPopup(
            planList = if (planList != null) planList else emptyList(),
            header = { PopupHeaderDate(popupDate.toDate()) },
            onDismissed = ::onListPopupDismissed,
            addButton = {
                AddButton(
                    onButtonClick = {onNavigateToEditPage(null,PlanType.SCHEDULE,popupDate.toDate())},
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.BottomEnd)
                )
            },
            onItemClick = ::openDetailPopup,
            onCheckboxClicked =
            { plan, checked ->
                val todo = plan as Todo
                monthlyViewModel.updatePlan(todo.copy(complete = !todo.complete))
            },
            onLeftButton = { popupDate = popupDate.afterDays(-1) },
            onRightButton = { popupDate = popupDate.afterDays(1) }
        )
        Log.d("BANG", "list popup opened")
    }


    //detail popup
    if(showDetailPopup){
        PlanDetailPopup(
            plan = popupPlan,
            header = { PopupHeaderTitle(popupPlan.title)},
            editButton = {
                EditButton(
                    plan = popupPlan,
                    onNavigateToEditPage = onNavigateToEditPage,
                    modifier=Modifier.align(Alignment.TopEnd)
                )
            },
            onDismissed = ::onDetailPopupDismissed
        )
        Log.d("BANG","detail popup opened")
    }

}


fun planListToHash(planList: List<Plan>): Hashtable<CalendarDay, List<Plan>> {
    val planOfMonth: Hashtable<CalendarDay, List<Plan>> = Hashtable()
    for (p in planList) {
        val days: List<CalendarDay> = when (p) {
            is Schedule -> {
                val start = p.startTime
                val end = p.endTime.afterDays(1)
                val days = mutableListOf<CalendarDay>()
                var day = start
                do {
                    days.add(day.toCalendarDay())
                    day = day.afterDays(1)
                } while (!day.equalDay(end))
                days
            }
            is Todo     -> List(1){p.dueTime.toCalendarDay()}
        }
        for(day in days){
            if (!planOfMonth!!.containsKey(day)) planOfMonth[day] = mutableListOf()

            val list = planOfMonth[day] as MutableList
            list!!.add(p)
        }

    }
    return planOfMonth
}


@Preview(showBackground = false, name = "Monthly Calendar Preview")
@Composable
fun MonthlyCalendarPreview() {

    MonthlyPageKT(
        monthlyViewModel = MonthlyViewModel(
            planRepository = DummyPlanRepository(),
        )
    ) { _, _, _ -> }
}