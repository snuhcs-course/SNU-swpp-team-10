package com.example.calendy.view.monthlyview

import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calendy.AppViewModelProvider
import com.example.calendy.R
import com.example.calendy.data.dummy.DummyCategoryRepository
import com.example.calendy.data.dummy.DummyPlanRepository
import com.example.calendy.data.dummy.DummyRepeatGroupRepository
import com.example.calendy.data.dummy.DummyScheduleRepository
import com.example.calendy.data.dummy.DummyTodoRepository
import com.example.calendy.data.plan.Plan
import com.example.calendy.data.plan.Todo
import com.example.calendy.utils.getWeekDay
import com.example.calendy.utils.toDate
import com.example.calendy.view.monthlyview.MonthlyDayPlanListAdaptor.OnItemClickEventListener
import com.prolificinteractive.materialcalendarview.CalendarDay

@Composable
fun MonthlyDayPlanListPopupKT(
    monthlyViewModel: MonthlyViewModel
        =viewModel(factory = AppViewModelProvider.Factory)
    ,onDismissRequest: ()-> Unit = {}
    ,selectedDate:CalendarDay = CalendarDay.today()
){

//    val planList : List<Plan> by monthlyViewModel.getPlanOfDay(selectedDate)!!.collectAsState()
    val planList : List<Plan> by monthlyViewModel.getAllPlans()!!.collectAsState()
    var recyclerView : RecyclerView
    var dateTextView : TextView
    var weekdayTextView : TextView
    var openDetailPopup by remember{ mutableStateOf(false) }

    var selectedPlan : Plan by remember { mutableStateOf(Todo(0,"",CalendarDay.today().toDate(),false,false,false,false,"",null,null,1,false,false) as Plan)}

    Dialog(onDismissRequest={onDismissRequest()}){

        AndroidView(
            modifier = Modifier.fillMaxWidth(),

            factory = { context ->
                val inflater = LayoutInflater.from(context)
                val view = inflater.inflate(R.layout.monthly_select_day_popup, null, false)
                view.apply {


                    //UI 객체생성
                    recyclerView = findViewById<RecyclerView>(R.id.monthlyDayPlanListRecycler)
                    dateTextView = findViewById<TextView>(R.id.planListDayNumber)
                    weekdayTextView = findViewById<TextView>(R.id.planListDayText)

                    // list recycler view
                    val linearLayoutManager = LinearLayoutManager(this.context)
                    recyclerView.layoutManager = linearLayoutManager // LayoutManager 설정


                }


            },
            update = {

                // set item onclick listener
                val dayPlanListAdaptor = MonthlyDayPlanListAdaptor(planList)

                dayPlanListAdaptor.setOnItemClickListener(OnItemClickEventListener { view, position ->
                    //TODO: open detail popup
                    selectedPlan = planList.get(position)
                    openDetailPopup=true
                    Log.d("calendy", "clicked item :$position")
                })
                recyclerView = it.findViewById(R.id.monthlyDayPlanListRecycler)
                dateTextView = it.findViewById(R.id.planListDayNumber)
                weekdayTextView = it.findViewById(R.id.planListDayText)

                recyclerView.adapter = dayPlanListAdaptor // 어댑터 설정


                // set header texts
                // TODO: implement date.getWeekDay
                weekdayTextView.text = selectedDate.getWeekDay()
                dateTextView.text = Integer.toString(selectedDate.getDay())

            }
        )
    }

    if(openDetailPopup)
        MonthlyDayPlanDetailPopupKT(
            onDismissRequest={openDetailPopup=false}
            , selectedPlan=selectedPlan
            )

}

@Preview(showBackground = false,name="List Popup preview")
@Composable
fun ListPopupPreview(){
    MonthlyDayPlanListPopupKT(
        monthlyViewModel = MonthlyViewModel(
            planRepository = DummyPlanRepository(),
            scheduleRepository = DummyScheduleRepository(),
            todoRepository = DummyTodoRepository(),
            categoryRepository = DummyCategoryRepository(),
            repeatGroupRepository = DummyRepeatGroupRepository(),
        )
        ,{}
    )
}