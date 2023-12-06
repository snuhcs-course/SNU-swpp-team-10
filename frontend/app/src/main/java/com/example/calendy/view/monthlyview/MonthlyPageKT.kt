package com.example.calendy.view.monthlyview


import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.example.calendy.utils.DateHelper
import com.example.calendy.utils.DateHelper.extract
import com.example.calendy.utils.afterDays
import com.example.calendy.utils.applyTime
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
import com.example.calendy.view.popup.PopupHeaderTitle
import com.example.calendy.view.popup.SwitchablePlanListPopup
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter
import com.prolificinteractive.materialcalendarview.format.DateFormatTitleFormatter
import com.prolificinteractive.materialcalendarview.format.MonthArrayTitleFormatter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun MonthlyPageKT(
    monthlyViewModel: MonthlyViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onNavigateToEditPage: (Int?, PlanType, Date?, Date?) -> Unit
) {
    val customMonths = stringArrayResource(id = R.array.custom_months)
    val customWeekdays = stringArrayResource(id = R.array.custom_weekdays)
    val uiState: MonthlyPageUIState by monthlyViewModel.uiState.collectAsState()
    val titleDecorators: List<TitleDecorator> by monthlyViewModel.titleDecorators.collectAsState()
    var selectedDayDecorator: SelectedDayDecorator? by remember { mutableStateOf(null) }
    var saturdayDecorator = SaturdayDecorator(uiState.currentMonth.month)
    var sundayDecorator = SundayDecorator(uiState.currentMonth.month)


    var popupDate by remember { mutableStateOf(uiState.selectedDate) }
    var showListPopup by remember { mutableStateOf(false) }


    fun openListPopup(selectedDate: CalendarDay) {
        popupDate = selectedDate
        showListPopup = true
    }


    fun onListPopupDismissed() {
        showListPopup = false
    }

    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {


        AndroidView(modifier = Modifier.fillMaxSize(), factory = { context ->
            val calendar = MaterialCalendarView(context)
            calendar.apply {

                // initial setting for calendar view
                setTitleFormatter(
                    DateFormatTitleFormatter(
                        SimpleDateFormat("yy년 MM월", Locale.KOREA)
                    )
                )
                setHeaderTextAppearance(R.style.CalendarHeader)
                setWeekDayFormatter(ArrayWeekDayFormatter(customWeekdays))
                setTileHeightDp(-1)
                setTopBarHeightDp(60)

                selectionColor = android.graphics.Color.TRANSPARENT
                selectedDate = uiState.selectedDate
                showOtherDates = MaterialCalendarView.SHOW_OTHER_MONTHS

                state().edit()
                    .setFirstDayOfWeek(Calendar.SUNDAY)
                    .setMinimumDate(DateHelper.worldStart().toCalendarDay())   //from 2000.1.1
                    .setMaximumDate(DateHelper.worldEnd().toCalendarDay()) //to 2030.12.31
                    .setCalendarDisplayMode(CalendarMode.MONTHS)
                    .setWeekdayBarHeightDp(40)
                    .commit();


                // event

                setOnDateChangedListener { widget, date, selected ->


                    if (date==uiState.selectedDate) {
                        openListPopup(date)
                    } else {
                        if (selectedDayDecorator!=null) removeDecorator(selectedDayDecorator!!)
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
                    saturdayDecorator, sundayDecorator, selectedDayDecorator, OneDayDecorator()
                )


                Log.d("BANG", "mcv initialize")
            }
        }, update = { mcv ->
            mcv.removeDecoratorsOfType(TitleDecorator::class.java)
            mcv.addDecorators(titleDecorators)

            Log.d("BANG", "mcv redraw")
            }
        )
        fun clickAction() {
            val currentTime = Calendar.getInstance()
            val startCalendar = Calendar.getInstance()
            val endCalendar = Calendar.getInstance()
            startCalendar.time = uiState.selectedDate.toDate()
            endCalendar.time = uiState.selectedDate.toDate()
            startCalendar.set(Calendar.HOUR_OF_DAY, currentTime.get(Calendar.HOUR_OF_DAY))
            endCalendar.set(Calendar.HOUR_OF_DAY, currentTime.get(Calendar.HOUR_OF_DAY))
            endCalendar.add(Calendar.HOUR_OF_DAY, 1)
            onNavigateToEditPage(null, PlanType.SCHEDULE, startCalendar.time, endCalendar.time)
        }
        FloatingActionButton(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd),
            onClick = {
                clickAction()
            },
            containerColor = Color(0xFF80ACFF),
            contentColor = androidx.compose.ui.graphics.Color.White,
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add")
        }
    }

    //list popup
    if (showListPopup) {
        val planList = uiState.planLabelContainer.getPlansAt(popupDate.toDate())
        SwitchablePlanListPopup(
            planList = if (planList!=null) planList else emptyList(),
            header = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    PopupHeaderDate(popupDate.toDate())
                    fun clickAction() {
                        val currentTime = Calendar.getInstance()
                        val startCalendar = Calendar.getInstance()
                        val endCalendar = Calendar.getInstance()
                        startCalendar.time = popupDate.toDate()
                        endCalendar.time = popupDate.toDate()
                        startCalendar.set(Calendar.HOUR_OF_DAY, currentTime.get(Calendar.HOUR_OF_DAY))
                        endCalendar.set(Calendar.HOUR_OF_DAY, currentTime.get(Calendar.HOUR_OF_DAY))
                        endCalendar.add(Calendar.HOUR_OF_DAY, 1)
                        onNavigateToEditPage(null, PlanType.SCHEDULE, startCalendar.time, endCalendar.time)
                    }
                    IconButton(
                        onClick = {clickAction()},
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "undo",
                            tint = Color(0xFF000000),
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
             },
            onDismissed = ::onListPopupDismissed,
            addButton = {
//                AddButton(
//                    onButtonClick = {
//                        val date = popupDate.toDate()
//                        val (_, _, _, hour, minute) = Date().extract()
//                        onNavigateToEditPage(
//                            null,
//                            PlanType.SCHEDULE,
//                            date.applyTime(hour, minute),
//                            null,
//                        )
//                    },
//                    modifier = Modifier
//                        .padding(8.dp)
//                        .align(Alignment.BottomEnd),
//                )
            },
            onItemClick = { plan ->
                onNavigateToEditPage(plan.id, plan.getPlanType(), null, null)
            },
            deletePlan = { plan ->
                monthlyViewModel.deletePlan(plan)
            },
            onCheckboxClicked = { plan, checked ->
                val todo = plan as Todo
                monthlyViewModel.updatePlan(todo.copy(complete = !todo.complete))
            },
            onLeftButton = { popupDate = popupDate.afterDays(-1) },
            onRightButton = { popupDate = popupDate.afterDays(1) },
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
    ) { _, _, _, _ -> }
}