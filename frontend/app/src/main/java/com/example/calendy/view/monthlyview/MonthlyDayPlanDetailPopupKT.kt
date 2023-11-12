package com.example.calendy.view.monthlyview

import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calendy.AppViewModelProvider
import com.example.calendy.R
import com.example.calendy.data.maindb.plan.Plan
import com.example.calendy.data.maindb.plan.PlanType
import com.example.calendy.data.maindb.plan.Schedule
import com.example.calendy.data.maindb.plan.Todo

@Composable
fun MonthlyDayPlanDetailPopupKT(
    monthlyViewModel: MonthlyViewModel = viewModel(factory = AppViewModelProvider.Factory)
    , onDismissRequest : ()->Unit = {}
    , selectedPlan: Plan,
    onNavigateToEditPage: (id: Int?, type: PlanType) -> Unit
){
//    val selectedPlan : Plan by monthlyViewModel.getPlanByID(selectedPlanId,planType)!!.collectAsState()

    Dialog(onDismissRequest = { onDismissRequest() }) {

        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                
            , factory ={
                context->
                val inflater = LayoutInflater.from(context)
                val view = inflater.inflate(R.layout.monthly_day_detail_popup, null, false)
                view.apply {

                    //UI 객체생성
                    val titleView = findViewById<TextView>(R.id.monthlyPlanDetailTitle)
                    val startTimeView = findViewById<TextView>(R.id.monthlyPlanDetailStartTime)
                    val endTimeView = findViewById<TextView>(R.id.monthlyPlanDetailEndTime)
                    val memoView = findViewById<TextView>(R.id.monthlyPlanDetailMemo)
                    val locationView = findViewById<TextView>(R.id.monthlyPlanDetailLocation)


                    //temporary code
                    titleView.setText(selectedPlan.title)
                    when(selectedPlan){
                        is Schedule ->
                        {
                            startTimeView.text = selectedPlan.startTime.toString()
                            endTimeView.text = selectedPlan.endTime.toString()
                        }

                        is Todo     ->
                        {
                            startTimeView.text = selectedPlan.dueTime.toString()
                        }
                    }
                    memoView.setText(selectedPlan.memo)

                    findViewById<Button>(R.id.editPlanButton).setOnClickListener {
                        when(selectedPlan) {
                            is Schedule -> onNavigateToEditPage(selectedPlan.id, PlanType.SCHEDULE)
                            else        -> onNavigateToEditPage(selectedPlan.id, PlanType.TODO)
                        }
                    }
                }
            },
            update = {

            }
        )
    }

}