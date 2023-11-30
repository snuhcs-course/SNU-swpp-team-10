package com.example.calendy.view.monthlyview

import android.graphics.Color
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.example.calendy.AppViewModelProvider
import com.example.calendy.R
import com.example.calendy.data.dummy.DummyPlanRepository
import com.example.calendy.data.maindb.plan.PlanType
import com.example.calendy.data.maindb.plan.Todo
import com.example.calendy.utils.afterDays
import com.example.calendy.utils.getPlanType
import com.example.calendy.utils.toCalendarDay
import com.example.calendy.utils.toDate
import com.example.calendy.view.monthlyview.decorator.OneDayDecorator
import com.example.calendy.view.monthlyview.decorator.SaturdayDecorator
import com.example.calendy.view.monthlyview.decorator.SelectedDayDecorator
import com.example.calendy.view.monthlyview.decorator.SundayDecorator
import com.example.calendy.view.monthlyview.decorator.TitleDecorator
import com.example.calendy.view.popup.AddButton
import com.example.calendy.view.popup.PopupHeaderDate
import com.example.calendy.view.popup.SwitchablePlanListPopup
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter
import com.prolificinteractive.materialcalendarview.format.MonthArrayTitleFormatter
import java.util.Calendar
import java.util.Date

@Composable
fun MonthlyPageKT(
    monthlyViewModel: MonthlyViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onNavigateToEditPage: (id: Int?, type: PlanType, date: Date?) -> Unit
) {
    val customMonths = stringArrayResource(id = R.array.custom_months)
    val customWeekdays = stringArrayResource(id = R.array.custom_weekdays)
    val uiState:MonthlyPageUIState by monthlyViewModel.uiState.collectAsState()
    val titleDecorators : List<TitleDecorator> by monthlyViewModel.titleDecorators.collectAsState()
    var selectedDayDecorator: SelectedDayDecorator? by remember { mutableStateOf(null) }
    var saturdayDecorator = SaturdayDecorator(uiState.currentMonth.month)
    var sundayDecorator = SundayDecorator(uiState.currentMonth.month)


    var popupDate by remember { mutableStateOf(uiState.selectedDate) }
    var showListPopup by remember { mutableStateOf(false) }


    fun openListPopup(selectedDate: CalendarDay)
    {
        popupDate=selectedDate
        showListPopup = true
    }


    fun onListPopupDismissed()
    {
        showListPopup=false
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {


        AndroidView(modifier = Modifier.fillMaxSize(), factory = { context ->
            val calendar = MaterialCalendarView(context)
            calendar.apply {

                // initial setting for calendar view
                setTitleFormatter(MonthArrayTitleFormatter(customMonths))
                setWeekDayFormatter(ArrayWeekDayFormatter(customWeekdays))
                setTileHeightDp(-1)
                selectionColor = Color.TRANSPARENT
                selectedDate = uiState.selectedDate
                showOtherDates = MaterialCalendarView.SHOW_OTHER_MONTHS
                state().edit()
                    .setFirstDayOfWeek(Calendar.SUNDAY)
                    .setMinimumDate(CalendarDay.from(2000, 0, 1))   //from 2000.1.1
                    .setMaximumDate(CalendarDay.from(2030, 11, 31)) //to 2030.12.31
                    .setCalendarDisplayMode(CalendarMode.MONTHS)
                    .commit();


                // event

                setOnDateChangedListener { widget, date, selected ->


                    if (date == uiState.selectedDate) {
                        openListPopup(date)
                    } else {
                        if (selectedDayDecorator != null)
                            removeDecorator(selectedDayDecorator!!)
                        selectedDayDecorator = SelectedDayDecorator(date, context)
                        addDecorator(selectedDayDecorator!!)
                        monthlyViewModel.setSelectedDate(date)
                    }


                }
                setOnMonthChangedListener { _, date ->
                    removeDecorator(saturdayDecorator)
                    removeDecorator(sundayDecorator)
                    saturdayDecorator = SaturdayDecorator(date.month)
                    sundayDecorator = SundayDecorator(date.month)
                    addDecorators(saturdayDecorator, sundayDecorator)
                    monthlyViewModel.setCurrentMonth(date)
                }

                // TODO : range selection by drag
//                setOnRangeSelectedListener{
//                    widget, dates ->
//                    val start = dates[0]
//                    val end = dates[dates.size-1]
//                    Log.d("BANG", "range selected : $start ~ $end")
//                }

                // decorator
                selectedDayDecorator = SelectedDayDecorator(uiState.selectedDate, context)
                addDecorators(
                    saturdayDecorator,
                    sundayDecorator,
                    selectedDayDecorator,
                    OneDayDecorator()
                )


                Log.d("BANG", "mcv initialize")
            }
        },
            update =
            { mcv ->
                mcv.removeDecoratorsOfType(TitleDecorator::class.java)
                mcv.addDecorators(titleDecorators)

                Log.d("BANG", "mcv redraw")
            }
        )

    }
        //list popup
    if (showListPopup) {
        val planList = uiState.planLabelContainer.getPlansAt(popupDate.toDate())
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
            onItemClick = { plan ->
                onNavigateToEditPage(plan.id, plan.getPlanType(), null)
            },
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