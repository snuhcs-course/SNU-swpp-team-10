package com.example.calendy.view.monthlyview

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calendy.AppViewModelProvider
import com.example.calendy.R
import com.example.calendy.data.plan.Plan
import com.example.calendy.data.plan.Schedule
import com.example.calendy.data.plan.Todo
import com.example.calendy.utils.toCalendarDay
import com.example.calendy.view.monthlyview.decorator.DotDecorator
import com.example.calendy.view.monthlyview.decorator.OneDayDecorator
import com.example.calendy.view.monthlyview.decorator.SaturdayDecorator
import com.example.calendy.view.monthlyview.decorator.SelectedDayDecorator
import com.example.calendy.view.monthlyview.decorator.SundayDecorator
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter
import com.prolificinteractive.materialcalendarview.format.MonthArrayTitleFormatter
import java.util.Calendar
import java.util.Hashtable

@Composable
fun MonthlyPageKT (monthlyViewModel: MonthlyViewModel = viewModel(factory = AppViewModelProvider.Factory)) {

    val custom_months = stringArrayResource(id = R.array.custom_months)
    val custom_weekdays = stringArrayResource(id = R.array.custom_weekdays)

    var selectedDate:CalendarDay = CalendarDay.today()
    var selectedDayDecorator :SelectedDayDecorator
    var planOfMonth: Hashtable<CalendarDay, MutableList<Plan>> = Hashtable( )
    var dotDecorator: DotDecorator? = null
    val oneDayDecorator = OneDayDecorator()

    val planList: List<Plan> by monthlyViewModel.getPlanListState(CalendarDay.from(2023,10,20),CalendarDay.from(2023,11,20)).collectAsState()

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = {
            context->
            val calendar = MaterialCalendarView(context)
            calendar.apply {

                // initial setting for calendar view
                setTitleFormatter(MonthArrayTitleFormatter(custom_months))
                setWeekDayFormatter(ArrayWeekDayFormatter(custom_weekdays))
                setSelectedDate(selectedDate)
                state().edit()
                    .setFirstDayOfWeek(Calendar.SUNDAY)
                    .setMinimumDate(CalendarDay.from(2000, 0, 1))   //from 2000.1.1
                    .setMaximumDate(CalendarDay.from(2030, 11, 31)) //to 2030.12.31
                    .setCalendarDisplayMode(CalendarMode.MONTHS)
                    .commit();

                // selected day decorator initialization

                // selected day decorator initialization
                selectedDayDecorator = SelectedDayDecorator(CalendarDay.today(), context)

                //temp dummy code

                //set dummy
//                val dummyDaySchedules: java.util.ArrayList<Schedule> = ArrayList<Schedule>()
//                dummyDaySchedules.add(Schedule(1231,"test1",Date(2023, 9, 11, 18, 0),Date(2023, 9, 11, 20, 0),"memomemo",1232,1,2,true,false))
//                dummyDaySchedules.add(Schedule(1232,"test2",Date(2023, 9, 11, 18, 0),Date(2023, 9, 11, 20, 0),"memomemo",1232,1,2,true,false))

//                val daySchedule: List<Schedule> = dummyDaySchedules
//                schedulesOfMonth!![CalendarDay.from(2023, 9, 11)] = daySchedule

                //add dot decorator
                planOfMonth = planListToHash(planList)
                dotDecorator = DotDecorator(planOfMonth)
                addDecorators(
                    SundayDecorator(),
                    SaturdayDecorator(),
                    oneDayDecorator,
                    selectedDayDecorator,
                    dotDecorator
                )

                setOnDateChangedListener(OnDateSelectedListener { widget, date, selected ->

                    // selected date changed
                    if (selectedDate == date && planOfMonth!!.containsKey(date)) openDayPlanListPopup(selectedDate)
                    removeDecorator(selectedDayDecorator)
                    selectedDate = date
                    selectedDayDecorator = SelectedDayDecorator(selectedDate, context)
                    addDecorators(selectedDayDecorator)
                })
                // event
                setOnMonthChangedListener(
                    // selected month changed
                    OnMonthChangedListener { widget, date ->

                    })
                Log.d("hochan","applied")

            }
        },
        update = {
            view->
//            planOfMonth= planListToHash(planList)
//            dotDecorator = DotDecorator(planOfMonth)
            Log.d("hochan","updated")
        }
        )

}


fun openDayPlanListPopup(selectedDate:CalendarDay) {
    //TODO: open popup
    Log.d("log","open detail popup for date : "+selectedDate)
}

fun planListToHash(planList:List<Plan>):Hashtable<CalendarDay,MutableList<Plan>>{
    val planOfMonth :Hashtable<CalendarDay,MutableList<Plan>> = Hashtable()
    for(p in planList){
        val day:CalendarDay =
            when(p){
                is Schedule -> p.startTime.toCalendarDay()
                is Todo -> p.dueTime.toCalendarDay()
            }
        if(!planOfMonth!!.containsKey(day))
            planOfMonth[day]= mutableListOf()

        val list = planOfMonth[day]
        list!!.add(p)
    }
    return planOfMonth
}