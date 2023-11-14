package com.example.calendy.view.weeklyview

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calendy.AppViewModelProvider
import com.example.calendy.R
import com.example.calendy.data.maindb.plan.PlanType
import com.example.calendy.view.monthlyview.MonthlyPageUIState
import com.example.calendy.view.monthlyview.decorator.OneDayDecorator
import com.example.calendy.view.monthlyview.decorator.SaturdayDecorator
import com.example.calendy.view.monthlyview.decorator.SelectedDayDecorator
import com.example.calendy.view.monthlyview.decorator.SundayDecorator
import com.example.calendy.view.monthlyview.decorator.TitleDecorator
import com.example.calendy.view.monthlyview.planListToHash
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter
import com.prolificinteractive.materialcalendarview.format.MonthArrayTitleFormatter
import java.util.Calendar
import java.util.Date
import java.util.Hashtable

@Composable
fun WeeklyPageKT (
//    weeklyViewModel: WeeklyViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onNavigateToEditPage: (id: Int?, type: PlanType, date: Date?) -> Unit = { _, _, _->}
){



    val custom_months = stringArrayResource(id = R.array.custom_months)
    val custom_weekdays = stringArrayResource(id = R.array.custom_weekdays)
//    val uiState: WeeklyViewModel by weeklyViewModel.uiState.collectAsState()

    var selectedDayDecorator: SelectedDayDecorator
//    var saturdayDecorator : SaturdayDecorator = SaturdayDecorator(uiState.currentMonth.month)
//    var sundayDecorator : SundayDecorator = SundayDecorator(uiState.currentMonth.month)
//
//    var planOfMonth: Hashtable<CalendarDay, List<Plan>> = planListToHash(uiState.plansOfMonth)


    AndroidView(modifier = Modifier.fillMaxSize(), factory = { context ->
        val calendar = MaterialCalendarView(context)
        calendar.apply {

            // initial setting for calendar view
            setTitleFormatter(MonthArrayTitleFormatter(custom_months))
            setWeekDayFormatter(ArrayWeekDayFormatter(custom_weekdays))
//            setTileHeightDp(-1)
            selectionColor = -1
//            setSelectedDate(uiState.selectedDate)
            showOtherDates = MaterialCalendarView.SHOW_OTHER_MONTHS
            state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2000, 0, 1))   //from 2000.1.1
                .setMaximumDate(CalendarDay.from(2030, 11, 31)) //to 2030.12.31
                .setCalendarDisplayMode(CalendarMode.WEEKS)
                .commit();
            setOnDateChangedListener { widget, date, selected ->

            }
            // event
            setOnMonthChangedListener(
                // selected month changed
                { widget, date ->
                    //TODO: change planList
                })

//            addView()

            Log.d("BANG", "mcv initialize")
        }
    },
        update =
        { mcv ->

            // selected day decorator initialization

            Log.d("BANG", "mcv redraw")
        }
    )
}

@Preview
@Composable
fun WeeklyPagePreview() {
    WeeklyPageKT()
}